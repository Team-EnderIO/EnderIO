package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface IOAwareConduitTicker<T extends ExtendedConduitData<T>> extends LoadedAwareConduitTicker<T> {
    @Override
    default void tickGraph(
        ConduitType<T> type,
        List<ConduitNode<T>> loadedNodes,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        ListMultimap<ColorControl, Connection<T>> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, Connection<T>> inserts = ArrayListMultimap.create();
        for (ConduitNode<T> node : loadedNodes) {
            for (Direction direction : Direction.values()) {
                node.getIOState(direction).ifPresent(ioState -> {
                    ioState
                        .extract()
                        .filter(extract -> isRedstoneMode(type, level, node.getPos(), ioState, coloredRedstoneProvider))
                        .ifPresent(
                            color -> extracts.get(color).add(new Connection<T>(
                                node.getPos(),
                                direction,
                                node.getExtendedConduitData(),
                                node.getUpgrade(direction),
                                node.getExtractFilter(direction),
                                node.getInsertFilter(direction))));
                    ioState
                        .insert()
                        .ifPresent(
                            color -> inserts.get(color).add(new Connection<T>(
                                node.getPos(),
                                direction,
                                node.getExtendedConduitData(),
                                node.getUpgrade(direction),
                                node.getExtractFilter(direction),
                                node.getInsertFilter(direction))));
                });
            }
        }
        for (ColorControl color : ColorControl.values()) {
            List<Connection<T>> extractList = extracts.get(color);
            List<Connection<T>> insertList = inserts.get(color);
            if (extractList.isEmpty() || insertList.isEmpty()) {
                continue;
            }

            tickColoredGraph(type, insertList, extractList, color, level, graph, coloredRedstoneProvider);
        }
    }

    void tickColoredGraph(
        ConduitType<T> type,
        List<Connection<T>> inserts,
        List<Connection<T>> extracts,
        ColorControl color,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isRedstoneMode(ConduitType<?> type, ServerLevel level, BlockPos pos, ConduitNode.IOState state,
        ColoredRedstoneProvider coloredRedstoneProvider) {
        if (!type.getMenuData().showRedstoneExtract()) {
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

    record Connection<T extends ExtendedConduitData<T>>(
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
