package com.enderio.enderio.content.conduits.type.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.network.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTicker;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemConduitTicker implements ConduitTicker<ItemConduit> {

    public static final ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    public void tick(ServerLevel level, ItemConduit conduit, ConduitNetwork network) {
        for (var channel : network.allChannels()) {
            tickChannel(conduit, network, channel);
        }
    }

    private void tickChannel(ItemConduit conduit, ConduitNetwork network, DyeColor channel) {
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
            var nodeData = extractConnection.node().getOrCreateNodeData(EIOConduitTypes.NodeData.ITEM.get());
            var connectionConfig = extractConnection.connectionConfig(EIOConduitTypes.ConnectionTypes.ITEM.get());

            // Get extraction filter
            var extractFilter = extractConnection.inventory()
                .getStackInSlot(ItemConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EnderIOCapabilities.ITEM_FILTER);

            int extracted = 0;
            int speed = conduit.transferRatePerCycle();

            nextItem: for (int i = 0; i < extractHandler.getSlots(); i++) {
                ItemStack extractedItem = extractHandler.extractItem(i, speed - extracted, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                // Ensure we cap the stack to its max size, even if the parent handler doesn't respect it.
                int extractedItemMaxStack = extractedItem.getMaxStackSize();
                if (extractedItem.getCount() > extractedItemMaxStack) {
                    extractedItem.setCount(extractedItemMaxStack);
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

                    // Prevent self-feeding
                    if (!connectionConfig.isSelfFeed() && extractConnection.equals(insertConnection)) {
                        continue;
                    }

                    // Try to insert into the target inventory
                    int successfullyInserted = tryInsertInto(insertConnection, extractedItem);

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

    /**
     * Try to insert item into the connected block.
     * @param insertConnection Connection to insert through
     * @param itemToInsert Item to be inserted
     * @return the amount successfully inserted.
     */
    private int tryInsertInto(ConduitBlockConnection insertConnection, ItemStack itemToInsert) {
        var insertHandler = insertConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK);
        if (insertHandler == null) {
            return 0;
        }

        var insertFilter = insertConnection.inventory()
            .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
            .getCapability(EnderIOCapabilities.ITEM_FILTER);

        if (insertFilter != null) {
            itemToInsert = insertFilter.test(
                insertConnection.getSidedCapability(Capabilities.ItemHandler.BLOCK), itemToInsert);
            if (itemToInsert.isEmpty()) {
                return 0;
            }
        }

        // Copy the stack to ensure extractedItem isn't modified by item handler implementations (Create Chute for example)
        ItemStack notInserted = ItemHandlerHelper.insertItem(insertHandler, itemToInsert.copy(), false);
        return itemToInsert.getCount() - notInserted.getCount();
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
