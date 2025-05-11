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
            toNextExtract: for (var extractConnection : network.extractConnections(channel)) {
                var insertConnections = network.insertConnectionsFrom(extractConnection);
                if (insertConnections.isEmpty()) {
                    continue;
                }

                // Get extract handler from the connection.
                IItemHandler extractHandler = extractConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                // Get node data for round robin index and connection config
                var nodeData = extractConnection.node().getOrCreateNodeData(ConduitTypes.NodeData.ITEM.get());
                var connectionConfig = extractConnection.connectionConfig(ConduitTypes.ConnectionTypes.ITEM.get());

                // Get extraction filter
                var extractFilter = extractConnection.inventory()
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

                        var insertHandler = insertConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK);
                        if (insertHandler == null) {
                            continue;
                        }

                        // Prevent self-feeding
                        if (!connectionConfig.isSelfFeed()
                                && extractConnection.connectionSide() == insertConnection.connectionSide()
                                && extractConnection.node() == insertConnection.node()) {
                            continue;
                        }

                        var insertFilter = insertConnection.inventory()
                                .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                                .getCapability(EIOCapabilities.ITEM_FILTER);

                        ItemStack itemToInsert = extractedItem.copy();
                        if (insertFilter != null) {
                            itemToInsert = insertFilter.test(
                                    insertConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK), itemToInsert);
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
    private boolean isEmpty(IItemHandler itemHandler, int afterIndex) {
        for (var i = afterIndex; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
