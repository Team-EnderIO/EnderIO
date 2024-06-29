package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraphContext;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public interface LoadedAwareConduitTicker<TOptions, TContext extends ConduitGraphContext<TContext>, TData extends ConduitData<TData>>
    extends ConduitTicker<TOptions, TContext, TData> {

    @Override
    default void tickGraph(ServerLevel level, ConduitType<TOptions, TContext, TData> type, ConduitGraph<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<ConduitNode<TContext, TData>> nodeIdentifiers = graph.getNodes()
            .stream().filter(node -> isLoaded(level, node.getPos()))
            .toList();

        tickGraph(level, type, nodeIdentifiers, graph, coloredRedstoneProvider);
    }

    void tickGraph(ServerLevel level, ConduitType<TOptions, TContext, TData> type,
        List<ConduitNode<TContext, TData>> loadedNodes, ConduitGraph<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
