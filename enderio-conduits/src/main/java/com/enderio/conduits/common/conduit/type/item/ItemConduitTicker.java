package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import com.enderio.conduits.common.init.ConduitTypes;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class ItemConduitTicker
        extends IOAwareConduitTicker<ItemConduit, ItemConduitConnectionConfig, ItemConduitTicker.Connection> {

    public static final ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    protected void tickColoredGraph(ServerLevel level, ItemConduit conduit, List<Connection> senders,
            List<Connection> receivers, DyeColor color, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {

        toNextExtract: for (Connection extract : receivers) {
            ItemConduitNodeData nodeData = extract.node().getOrCreateNodeData(ConduitTypes.NodeData.ITEM.get());

            // Prioritize senders in order of priority and distance.
            var prioritizedSenders = senders.stream()
                    .sorted(Comparator.comparingInt((Connection c) -> c.config().priority())
                            .reversed()
                            .thenComparingDouble(e -> e.pos().distSqr(extract.pos())))
                    .toList();

            // Get extraction filter.
            var extractFilter = extract.inventory()
                    .getStackInSlot(ItemConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EIOCapabilities.ITEM_STACK_FILTER);

            IItemHandler extractHandler = extract.itemHandler();
            int extracted = 0;

            int speed = conduit.transferRatePerCycle();

            nextItem: for (int i = 0; i < extractHandler.getSlots(); i++) {
                ItemStack extractedItem = extractHandler.extractItem(i, speed - extracted, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                if (extractFilter != null) {
                    extractedItem = extractFilter.test(extract.itemHandler, extractedItem);

                    if (extractedItem.isEmpty()) {
                        continue;
                    }
                }

                var connectionConfig = extract.node()
                        .getConnectionConfig(extract.side(), ConduitTypes.ConnectionTypes.ITEM.get());

                int startingIndex = 0;
                if (connectionConfig.isRoundRobin()) {
                    startingIndex = nodeData.getIndex(extract.side());
                    if (prioritizedSenders.size() <= startingIndex) {
                        startingIndex = 0;
                    }
                }

                for (int j = startingIndex; j < startingIndex + prioritizedSenders.size(); j++) {
                    ItemStack itemToInsert = extractedItem.copy();

                    int insertIndex = j % prioritizedSenders.size();
                    Connection insert = prioritizedSenders.get(insertIndex);

                    if (!connectionConfig.isSelfFeed() && extract.side() == insert.side()
                            && extract.node().getPos().equals(insert.node().getPos())) {
                        continue;
                    }

                    var insertFilter = insert.inventory()
                            .getStackInSlot(ItemConduit.INSERT_FILTER_SLOT)
                            .getCapability(EIOCapabilities.ITEM_STACK_FILTER);

                    if (insertFilter != null) {
                        itemToInsert = insertFilter.test(insert.itemHandler, itemToInsert);

                        if (itemToInsert.isEmpty()) {
                            continue;
                        }
                    }

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.itemHandler, itemToInsert, false);
                    int successfullyInserted = itemToInsert.getCount() - notInserted.getCount();

                    if (successfullyInserted > 0) {
                        extracted += successfullyInserted;
                        extractHandler.extractItem(i, successfullyInserted, false);
                        if (extracted >= speed || isEmpty(extractHandler, i + 1)) {
                            if (connectionConfig.isRoundRobin()) {
                                nodeData.setIndex(extract.side(), insertIndex + 1);
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

    private boolean isEmpty(IItemHandler itemHandler, int afterIndex) {
        for (var i = afterIndex; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // TODO: I think this is wrong.
    @Override
    protected void preProcessReceivers(List<Connection> receivers) {
        receivers.sort(Comparator.comparingInt((Connection n) -> n.config().priority()).reversed());
    }

    @Override
    @Nullable
    protected Connection createConnection(Level level, ConduitNode node, Direction side) {
        IItemHandler itemHandler = node.getNeighbourCapability(Capabilities.ItemHandler.BLOCK, side);
        if (itemHandler != null) {
            return new Connection(node, side, node.getConnectionConfig(side, ItemConduitConnectionConfig.TYPE),
                    itemHandler);
        }

        return null;
    }

    protected static class Connection extends SimpleConnection<ItemConduitConnectionConfig> {
        private final IItemHandler itemHandler;

        public Connection(ConduitNode node, Direction side, ItemConduitConnectionConfig config,
                IItemHandler itemHandler) {
            super(node, side, config);
            this.itemHandler = itemHandler;
        }

        public IItemHandler itemHandler() {
            return itemHandler;
        }
    }
}
