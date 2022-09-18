package com.enderio.api.conduit.ticker;

import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface IConduitTicker {

    void tickGraph(Graph<Mergeable.Dummy> graph, ServerLevel level);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);
}
