package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ExtendedConduitData;
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

public interface LoadedAwareConduitTicker<T extends ExtendedConduitData<T>> extends ConduitTicker<T> {

    @Override
    default void tickGraph(ConduitType<T> type, Graph<Mergeable.Dummy> graph, ServerLevel level,
        ColoredRedstoneProvider coloredRedstoneProvider) {
        List<ConduitNode<T>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof ConduitNode<?> node && isLoaded(level, node.getPos())) {
                //noinspection unchecked
                nodeIdentifiers.add((ConduitNode<T>) node);
            }
        }

        tickGraph(type, nodeIdentifiers, level, graph, coloredRedstoneProvider);
    }

    void tickGraph(ConduitType<T> type, List<ConduitNode<T>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
