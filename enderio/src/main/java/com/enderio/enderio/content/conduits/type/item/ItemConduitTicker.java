package com.enderio.enderio.content.conduits.type.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.enderio.init.EIOConduitTypes;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Map;

public class ItemConduitTicker extends ConduitTickerBase<ItemConduit> {

    public static final ItemConduitTicker INSTANCE = new ItemConduitTicker();

    private ItemConduitTicker() {
        super(EIOConduitTypes.ITEM::get);
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits) {
        Map<ConduitConnectionPath, Integer> insertedPerPath = Maps.newHashMap();

        for (var channel : network.allChannels()) {
            tickChannel(network, tickableConduits, insertedPerPath, channel);
        }
    }

    private void tickChannel(ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits, Map<ConduitConnectionPath, Integer> insertedPerPath, DyeColor channel) {
        toNextExtract: for (var extractConnection : network.extractConnections(channel)) {
            var insertPaths = network.insertConnectionsFrom(extractConnection);
            if (insertPaths.isEmpty()) {
                continue;
            }

            var extractConduit = extractConnection.node().conduit(conduitType());

            // If this conduit isn't allowed to tick, skip it.
            if (!tickableConduits.contains(extractConduit)) {
                continue;
            }

            // Get extract handler from the connection.
            ResourceHandler<ItemResource> extractHandler = extractConnection.getSidedCapability(Capabilities.Item.BLOCK);
            if (extractHandler == null) {
                continue;
            }

            // Get node data for round robin index and connection config
            var nodeData = extractConnection.node().getOrCreateNodeData(EIOConduitTypes.NodeData.ITEM.get());
            var connectionConfig = extractConnection.connectionConfig(EIOConduitTypes.ConnectionTypes.ITEM.get());

            // Get extraction filter
            var extractFilter = extractConnection.inventory()
                .getStackInSlot(ItemConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EnderIOCapabilities.ITEM_FILTER);

            int totalExtracted = 0;
            int speed = extractConduit.value().transferRatePerCycle();

            nextItem: for (int i = 0; i < extractHandler.size(); i++) {
                try (Transaction transaction = Transaction.openRoot()) {
                    ItemResource itemResource = extractHandler.getResource(i);
                    if (itemResource.isEmpty()) {
                        continue;
                    }

                    int extracted = extractHandler.extract(i, itemResource, speed - totalExtracted, transaction);
                    if (extracted <= 0) {
                        continue;
                    }

                    // Ensure we cap the stack to its max size, even if the parent handler doesn't respect it.
                    int extractedItemMaxStack = itemResource.toStack(extracted).getMaxStackSize();
                    if (extracted > extractedItemMaxStack) {
                        extracted = extractedItemMaxStack;
                    }

                    if (extractFilter != null) {
                        var filteredStack = extractFilter.test(extractHandler, itemResource.toStack(extracted));
                        if (filteredStack.isEmpty()) {
                            continue;
                        }

                        extracted = filteredStack.getCount();
                    }

                    int startingIndex = 0;
                    if (connectionConfig.isRoundRobin()) {
                        startingIndex = nodeData.getIndex(extractConnection.connectionSide());
                        if (insertPaths.size() <= startingIndex) {
                            startingIndex = 0;
                        }
                    }

                    for (int j = startingIndex; j < startingIndex + insertPaths.size(); j++) {
                        int senderIndex = j % insertPaths.size();
                        var insertPath = insertPaths.get(senderIndex);
                        var insertConnection = insertPath.end();

                        // Get the adjusted speed (as some conduits tick at differing speeds)
                        var pathSpeedAndTickRate = insertPath.property(ItemConduit.PATH_SPEED_AND_TICK_RATE);
                        final int maxSpeed = pathSpeedAndTickRate.getAdjustedSpeed(extractConduit.value().networkTickRate());

                        // Calculate remaining 'speed' for this path
                        int remaining = maxSpeed - insertedPerPath.getOrDefault(insertPath, 0);
                        if (remaining <= 0) {
                            continue;
                        }

                        var insertHandler = insertConnection.getSidedCapability(Capabilities.Item.BLOCK);
                        if (insertHandler == null) {
                            continue;
                        }

                        // Prevent self-feeding
                        if (!connectionConfig.isSelfFeed() && extractConnection.connectionSide() == insertConnection.connectionSide()
                            && extractConnection.node() == insertConnection.node()) {
                            continue;
                        }

                        int amountToInsert = extracted;

                        // Limit to path's max speed
                        if (extracted > remaining) {
                            extracted = remaining;
                        }

                        var insertFilter = insertConnection
                            .inventory()
                            .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                            .getCapability(EnderIOCapabilities.ITEM_FILTER);

                        if (insertFilter != null) {
                            var filteredStack = insertFilter.test(insertHandler, itemResource.toStack(amountToInsert));
                            if (filteredStack.isEmpty()) {
                                continue;
                            }

                            amountToInsert = filteredStack.getCount();
                        }

                        int inserted = ResourceHandlerUtil.insertStacking(insertHandler, itemResource, amountToInsert, transaction);

                        if (inserted > 0) {
                            transaction.commit();
                            totalExtracted += inserted;

                            // Track how much was inserted through this path.
                            insertedPerPath.compute(insertPath, (k, v) -> v == null ? inserted : v + inserted);

                            if (totalExtracted >= speed || isEmpty(extractHandler, i + 1)) {
                                if (connectionConfig.isRoundRobin()) {
                                    nodeData.setIndex(extractConnection.connectionSide(), senderIndex + 1);
                                }
                                continue toNextExtract;
                            } else {
                                continue nextItem;
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: is this necessary?
    private boolean isEmpty(ResourceHandler<?> handler, int afterIndex) {
        int size = handler.size();

        for(int i = afterIndex; i < size; ++i) {
            if (handler.getAmountAsLong(i) > 0L) {
                return false;
            }
        }

        return true;
    }
}
