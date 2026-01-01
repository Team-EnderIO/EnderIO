package com.enderio.enderio.content.conduits.type.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTicker;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ItemConduitTicker implements ConduitTicker<ItemConduit> {

    public static final ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    public void tick(ServerLevel level, ItemConduit conduit, ConduitNetwork network) {
        for (var channel : network.allChannels()) {
            toNextExtract: for (var extractConnection : network.extractConnections(channel)) {
                var insertConnections = network.insertConnectionsFrom(extractConnection);
                if (insertConnections.isEmpty()) {
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
                int speed = conduit.transferRatePerCycle();

                nextItem: for (int i = 0; i < extractHandler.size(); i++) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        ItemResource itemResource = extractHandler.getResource(i);
                        int extracted = extractHandler.extract(i, itemResource, speed - totalExtracted, transaction);
                        if (extracted <= 0) {
                            continue;
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
                            if (insertConnections.size() <= startingIndex) {
                                startingIndex = 0;
                            }
                        }

                        for (int j = startingIndex; j < startingIndex + insertConnections.size(); j++) {
                            int senderIndex = j % insertConnections.size();
                            var insertConnection = insertConnections.get(senderIndex);

                            var insertHandler = insertConnection.getSidedCapability(Capabilities.Item.BLOCK);
                            if (insertHandler == null) {
                                continue;
                            }

                            // Prevent self-feeding
                            if (!connectionConfig.isSelfFeed()
                                    && extractConnection.connectionSide() == insertConnection.connectionSide()
                                    && extractConnection.node() == insertConnection.node()) {
                                continue;
                            }

                            int amountToInsert = extracted;

                            var insertFilter = insertConnection.inventory()
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
                                extracted += inserted;
                                if (extracted >= speed || isEmpty(extractHandler, i + 1)) {
                                    if (connectionConfig.isRoundRobin()) {
                                        nodeData.setIndex(extractConnection.connectionSide(), senderIndex + 1);
                                    }
                                    continue toNextExtract;
                                } else {
                                    continue nextItem;
                                }
                            }
                        }

                        transaction.commit();
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
