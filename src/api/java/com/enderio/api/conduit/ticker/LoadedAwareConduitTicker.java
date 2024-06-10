package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public interface LoadedAwareConduitTicker<T extends ConduitData<T>> extends ConduitTicker<T> {

    @Override
    default void tickGraph(ServerLevel level, ConduitType<T> type, ConduitGraph<T> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<ConduitNode<T>> nodeIdentifiers = graph.getNodes()
            .stream().filter(node -> isLoaded(level, node.getPos()))
            .toList();

        tickGraph(level, type, nodeIdentifiers, graph, coloredRedstoneProvider);
    }

    void tickGraph(ServerLevel level, ConduitType<T> type,
        List<ConduitNode<T>> loadedNodes, ConduitGraph<T> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
