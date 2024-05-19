package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;

public interface LoadedAwareConduitTicker extends ConduitTicker {

    @Override
    default void tickGraph(ConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<ConduitNode<?>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof ConduitNode<?> node && isLoaded(level, node.getPos())) {
                nodeIdentifiers.add(node);
            }
        }

        tickGraph(type, nodeIdentifiers, level, graph, isRedstoneActive);
    }

    void tickGraph(ConduitType<?> type, List<ConduitNode<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
