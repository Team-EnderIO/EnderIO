package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public interface LoadedAwareConduitTicker<TConduit extends Conduit<TConduit>> extends ConduitTicker<TConduit> {

    @Override
    default void tickGraph(ServerLevel level, TConduit conduit, ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {
        List<ConduitNode> nodeIdentifiers = graph.getNodes()
            .stream().filter(node -> isLoaded(level, node.getPos()))
            .toList();

        tickGraph(level, conduit, nodeIdentifiers, graph, coloredRedstoneProvider);
    }

    void tickGraph(ServerLevel level, TConduit type,
        List<ConduitNode> loadedNodes, ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    default boolean isLoaded(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.shouldTickBlocksAt(pos);
    }
}
