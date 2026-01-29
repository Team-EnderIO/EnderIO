package com.enderio.enderio.content.conduits.type.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class NewItemConduitTicker extends ConduitTickerBase<ItemConduit> {

    public static final NewItemConduitTicker INSTANCE = new NewItemConduitTicker();

    private NewItemConduitTicker() {}

    @Override
    protected ConduitType<ItemConduit> conduitType() {
        return EIOConduitTypes.ITEM.get();
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, int tickOffset) {
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
                var nodeData = extractConnection.node().getOrCreateNodeData(EIOConduitTypes.NodeData.ITEM.get());
                var connectionConfig = extractConnection.connectionConfig(EIOConduitTypes.ConnectionTypes.ITEM.get());

                // Get extraction filter
                var extractFilter = extractConnection.inventory()
                    .getStackInSlot(ItemConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EnderIOCapabilities.ITEM_FILTER);

                var extractConduit = extractConnection.node().conduit(conduitType());

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

                        ItemStack itemToInsert = extractedItem.copy();

                        // Restrict to the speed of the inserting conduit.
                        // TODO: When we add path speeds, we'll restrict to the path speed instead.
                        var insertConduit = insertConnection.node().conduit(conduitType());
                        int maxSpeed = insertConduit.value().transferRatePerCycle();
                        if (itemToInsert.getCount() > maxSpeed) {
                            itemToInsert.setCount(maxSpeed);
                        }

                        var insertFilter = insertConnection.inventory()
                            .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                            .getCapability(EnderIOCapabilities.ITEM_FILTER);

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

    @Override
    protected int getTickRate(ConduitNetwork network) {
        return network.conduit().value().networkTickRate();
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
