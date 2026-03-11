package com.enderio.enderio.content.conduits.type.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.enderio.init.EIOConduitTypes;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.slf4j.Logger;

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
                IItemHandler extractHandler = extractConnection.getSidedCapability(ForgeCapabilities.ITEM_HANDLER);
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

                int extracted = 0;
                int speed = extractConduit.value().transferRatePerCycle();

                nextItem: for (int i = 0; i < extractHandler.getSlots(); i++) {
                    ItemStack extractedItem = extractHandler.extractItem(i, speed - extracted, true);
                    if (extractedItem.isEmpty()) {
                        continue;
                    }

                    if (extractFilter != null) {
                        extractedItem = extractFilter.test(extractHandler, extractedItem);
                        if (extractedItem.isEmpty()) {
                            continue;
                        }
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

                        var insertHandler = insertConnection.getSidedCapability(ForgeCapabilities.ITEM_HANDLER);
                        if (insertHandler == null) {
                            continue;
                        }

                        // Prevent self-feeding
                        if (!connectionConfig.isSelfFeed()
                            && extractConnection.connectionSide() == insertConnection.connectionSide()
                            && extractConnection.node() == insertConnection.node()) {
                            continue;
                        }

                        ItemStack itemToInsert = extractedItem.copy();

                        // Limit to path's max speed
                        if (itemToInsert.getCount() > remaining) {
                            itemToInsert.setCount(remaining);
                        }

                        var insertFilter = insertConnection.inventory()
                            .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                            .getCapability(EnderIOCapabilities.ITEM_FILTER);

                        if (insertFilter != null) {
                            itemToInsert = insertFilter.test(
                                insertConnection.getSidedCapability(ForgeCapabilities.ITEM_HANDLER), itemToInsert);
                            if (itemToInsert.isEmpty()) {
                                continue;
                            }
                        }

                        ItemStack notInserted = ItemHandlerHelper.insertItem(insertHandler, itemToInsert, false);
                        int successfullyInserted = itemToInsert.getCount() - notInserted.getCount();

                        if (successfullyInserted > 0) {
                            extracted += successfullyInserted;
                            extractHandler.extractItem(i, successfullyInserted, false);

                            // Track how much was inserted through this path.
                            insertedPerPath.compute(insertPath, (k, v) -> v == null ? successfullyInserted : v + successfullyInserted);

                            if (connectionConfig.isRoundRobin()) {
                                nodeData.setIndex(extractConnection.connectionSide(), senderIndex + 1);
                            }

                            if (extracted >= speed || isEmpty(extractHandler, i + 1)) {
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
    private boolean isEmpty(IItemHandler itemHandler, int afterIndex) {
        for (var i = afterIndex; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
