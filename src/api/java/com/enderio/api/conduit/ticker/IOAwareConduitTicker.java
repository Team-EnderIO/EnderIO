package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.ConduitNode;
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

public interface IOAwareConduitTicker extends LoadedAwareConduitTicker {
    @Override
    default void tickGraph(ConduitType<?> type, List<ConduitNode<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        ListMultimap<ColorControl, Connection> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, Connection> inserts = ArrayListMultimap.create();
        for (ConduitNode<?> node : loadedNodes) {
            for (Direction direction : Direction.values()) {
                node.getIOState(direction).ifPresent(ioState -> {
                    ioState
                        .extract()
                        .filter(extract -> isRedstoneMode(type, level, node.getPos(), ioState, isRedstoneActive))
                        .ifPresent(
                            color -> extracts.get(color).add(new Connection(
                                node.getPos(),
                                direction,
                                node.getExtendedConduitData(),
                                node.getUpgrade(direction),
                                node.getExtractFilter(direction),
                                node.getInsertFilter(direction))));
                    ioState
                        .insert()
                        .ifPresent(
                            color -> inserts.get(color).add(new Connection(
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
            List<Connection> extractList = extracts.get(color);
            List<Connection> insertList = inserts.get(color);
            if (extractList.isEmpty() || insertList.isEmpty()) {
                continue;
            }

            tickColoredGraph(type, insertList, extractList, color, level, graph, isRedstoneActive);
        }
    }

    void tickColoredGraph(ConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive);

    default boolean isRedstoneMode(ConduitType<?> type, ServerLevel level, BlockPos pos, ConduitNode.IOState state,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
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

        return state.control().isActive(hasRedstone || isRedstoneActive.apply(level, pos, state.redstoneChannel()));
    }

    record Connection(
        BlockPos pos,
        Direction dir,
        ExtendedConduitData<?> data,
        @Nullable ConduitUpgrade upgrade,
        @Nullable Predicate<?> extractFilter,
        @Nullable Predicate<?> insertFilter) {
        public BlockPos move() {
            return pos.relative(dir);
        }
    }
}
