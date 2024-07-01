package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.Conduit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public interface LoadedAwareConduitTicker<TConduit extends Conduit<TConduit, TContext, TData>, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>>
    extends ConduitTicker<TConduit, TContext, TData> {

    @Override
    default void tickGraph(ServerLevel level, TConduit conduit, ConduitNetwork<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<ConduitNode<TContext, TData>> nodeIdentifiers = graph.getNodes()
            .stream().filter(node -> isLoaded(level, node.getPos()))
            .toList();

        tickGraph(level, conduit, nodeIdentifiers, graph, coloredRedstoneProvider);
    }

    void tickGraph(ServerLevel level, TConduit type,
        List<ConduitNode<TContext, TData>> loadedNodes, ConduitNetwork<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
