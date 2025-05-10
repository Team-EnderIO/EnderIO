package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.conduits.common.init.ConduitTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemConduitTicker implements ConduitTicker<ItemConduit> {

    public static final ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    public void tick(ServerLevel level, ItemConduit conduit, IConduitNetwork network) {
        for (var channel : network.allChannels()) {
            toNextExtract: for (var receivingConnection : network.receivingConnections(channel)) {
                // Get extract handler from the connection.
                IItemHandler extractHandler = receivingConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                // Get node data for round robin index and connection config
                var nodeData = receivingConnection.node().getOrCreateNodeData(ConduitTypes.NodeData.ITEM.get());
                var connectionConfig = receivingConnection.connectionConfig(ConduitTypes.ConnectionTypes.ITEM.get());

                // Get extraction filter
                var extractFilter = receivingConnection.inventory()
                        .getStackInSlot(ItemConduit.EXTRACT_FILTER_SLOT)
                        .getCapability(EIOCapabilities.ITEM_FILTER);

                int extracted = 0;
                int speed = conduit.transferRatePerCycle();

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

                    var allSenders = network.sendingConnectionsFrom(receivingConnection);

                    int startingIndex = 0;
                    if (connectionConfig.isRoundRobin()) {
                        startingIndex = nodeData.getIndex(receivingConnection.connectionSide());
                        if (allSenders.size() <= startingIndex) {
                            startingIndex = 0;
                        }
                    }

                    for (int j = startingIndex; j < startingIndex + allSenders.size(); j++) {
                        int senderIndex = j % allSenders.size();
                        var sendingConnection = allSenders.get(senderIndex);

                        var insertHandler = sendingConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK);
                        if (insertHandler == null) {
                            continue;
                        }

                        // Prevent self-feeding
                        if (!connectionConfig.isSelfFeed()
                                && receivingConnection.connectionSide() == sendingConnection.connectionSide()
                                && receivingConnection.node() == sendingConnection.node()) {
                            continue;
                        }

                        var insertFilter = sendingConnection.inventory()
                                .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                                .getCapability(EIOCapabilities.ITEM_FILTER);

                        ItemStack itemToInsert = extractedItem.copy();
                        if (insertFilter != null) {
                            itemToInsert = insertFilter.test(
                                    sendingConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK), itemToInsert);
                            if (itemToInsert.isEmpty()) {
                                continue;
                            }
                        }

                        ItemStack notInserted = ItemHandlerHelper.insertItem(insertHandler, itemToInsert, false);
                        int successfullyInserted = itemToInsert.getCount() - notInserted.getCount();

                        if (successfullyInserted > 0) {
                            extracted += successfullyInserted;
                            extractHandler.extractItem(i, successfullyInserted, false);
                            if (extracted >= speed || isEmpty(extractHandler, i + 1)) {
                                if (connectionConfig.isRoundRobin()) {
                                    nodeData.setIndex(receivingConnection.connectionSide(), senderIndex + 1);
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
    private boolean isEmpty(IItemHandler itemHandler, int afterIndex) {
        for (var i = afterIndex; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
