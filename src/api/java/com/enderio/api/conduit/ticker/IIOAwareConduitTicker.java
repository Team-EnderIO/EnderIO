package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public interface IIOAwareConduitTicker extends ILoadedAwareConduitTicker {
    @Override
    default void tickGraph(IConduitType<?> type, List<NodeIdentifier<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        ListMultimap<ColorControl, Connection> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, Connection> inserts = ArrayListMultimap.create();
        for (GraphObject<Mergeable.Dummy> object : loadedNodes) {
            if (object instanceof NodeIdentifier<?> nodeIdentifier) {
                for (Direction direction: Direction.values()) {
                    nodeIdentifier.getIOState(direction)
                        .ifPresent(ioState -> {
                            ioState.extract().filter(extract -> isRedstoneMode(type, level, nodeIdentifier.getPos(), ioState, isRedstoneActive)).ifPresent(color -> extracts.get(color).add(new Connection(nodeIdentifier.getPos(), direction, nodeIdentifier.getExtendedConduitData())));
                            ioState.insert().ifPresent(color -> inserts.get(color).add(new Connection(nodeIdentifier.getPos(), direction, nodeIdentifier.getExtendedConduitData())));
                    });
                }
            }
        }
        for (ColorControl color: ColorControl.values()) {
            List<Connection> extractList = extracts.get(color);
            List<Connection> insertList = inserts.get(color);
            if (extractList.isEmpty() || insertList.isEmpty())
                continue;
            tickColoredGraph(type, insertList, extractList, color, level, graph, isRedstoneActive);
        }
    }

    void tickColoredGraph(IConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive);
    default boolean isRedstoneMode(IConduitType<?> type, ServerLevel level, BlockPos pos, NodeIdentifier.IOState state, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        if (!type.getMenuData().showRedstoneExtract())
            return true;
        if (state.control() == RedstoneControl.ALWAYS_ACTIVE)
            return true;
        if (state.control() == RedstoneControl.NEVER_ACTIVE)
            return false;
        boolean hasRedstone = false;
        for (Direction direction: Direction.values()) {
            if (level.getSignal(pos.relative(direction), direction) > 0) {
                hasRedstone = true;
                break;
            }
        }
        return state.control().isActive(hasRedstone || isRedstoneActive.apply(level, pos, state.redstoneChannel()));
    }
    record Connection(BlockPos pos, Direction dir, IExtendedConduitData<?> data) {
        public BlockPos move() {
            return pos.relative(dir);
        }
    }
}
