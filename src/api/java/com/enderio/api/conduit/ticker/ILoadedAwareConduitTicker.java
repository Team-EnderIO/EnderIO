package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.NodeIdentifier;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public interface ILoadedAwareConduitTicker extends IConduitTicker {

    @Override
    default void tickGraph(Graph<Mergeable.Dummy> graph, ServerLevel level) {

        List<NodeIdentifier<?>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof NodeIdentifier<?> node && isLoaded(level, node.getPos())) {
                nodeIdentifiers.add(node);
            }
        }
        tickGraph(nodeIdentifiers, level);
    }

    void tickGraph(List<NodeIdentifier<?>> loadedNodes, ServerLevel level);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
