package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IOAwareConduitTicker<TConduit extends Conduit<TConduit, TContext, TData>, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>> extends LoadedAwareConduitTicker<TConduit, TContext, TData> {
    @Override
    default void tickGraph(ServerLevel level, TConduit conduit,
        List<ConduitNode<TContext, TData>> loadedNodes, ConduitNetwork<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        ListMultimap<ColorControl, Connection<TData>> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, Connection<TData>> inserts = ArrayListMultimap.create();
        for (ConduitNode<TContext, TData> node : loadedNodes) {
            for (Direction direction : Direction.values()) {
                node.getIOState(direction).ifPresent(ioState -> {
                    ioState
                        .extract()
                        .filter(extract -> isRedstoneMode(conduit, level, node.getPos(), ioState, coloredRedstoneProvider))
                        .ifPresent(
                            color -> extracts.get(color).add(new Connection<>(
                                node.getPos(),
                                direction,
                                node.getConduitData(),
                                node.getUpgrade(direction),
                                node.getExtractFilter(direction),
                                node.getInsertFilter(direction))));
                    ioState
                        .insert()
                        .ifPresent(
                            color -> inserts.get(color).add(new Connection<>(
                                node.getPos(),
                                direction,
                                node.getConduitData(),
                                node.getUpgrade(direction),
                                node.getExtractFilter(direction),
                                node.getInsertFilter(direction))));
                });
            }
        }
        for (ColorControl color : ColorControl.values()) {
            List<Connection<TData>> extractList = extracts.get(color);
            List<Connection<TData>> insertList = inserts.get(color);
            if (shouldSkipColor(extractList, insertList)) {
                continue;
            }

            tickColoredGraph(level, conduit, insertList, extractList, color, graph, coloredRedstoneProvider);
        }
    }

    default boolean shouldSkipColor(List<Connection<TData>> extractList, List<Connection<TData>> insertList) {
        return extractList.isEmpty() || insertList.isEmpty();
    }

    void tickColoredGraph(
        ServerLevel level,
        TConduit conduit,
        List<Connection<TData>> inserts,
        List<Connection<TData>> extracts,
        ColorControl color,
        ConduitNetwork<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isRedstoneMode(TConduit conduit, ServerLevel level, BlockPos pos, ConduitNode.IOState state,
        ColoredRedstoneProvider coloredRedstoneProvider) {
        if (!conduit.getMenuData().showRedstoneExtract()) {
            return true;
        }

        if (state.control() == RedstoneControl.ALWAYS_ACTIVE) {
            return true;
        }

        if (state.control() == RedstoneControl.NEVER_ACTIVE) {
            return false;
        }

        boolean hasRedstone = false;
        for (Direction direction : Direction.values()) {
            if (level.getSignal(pos.relative(direction), direction) > 0) {
                hasRedstone = true;
                break;
            }
        }

        return state.control().isActive(hasRedstone || coloredRedstoneProvider.isRedstoneActive(level, pos, state.redstoneChannel()));
    }

    record Connection<T extends ConduitData<T>>(
        BlockPos pos,
        Direction dir,
        T data,
        @Nullable ConduitUpgrade upgrade,
        @Nullable ResourceFilter extractFilter,
        @Nullable ResourceFilter insertFilter) {
        public BlockPos move() {
            return pos.relative(dir);
        }
    }
}
