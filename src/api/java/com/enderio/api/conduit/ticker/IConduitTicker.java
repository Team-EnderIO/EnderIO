package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.IConduitType;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface IConduitTicker {

    void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);

    /**
     *
     * @return if this is not always able to determine connectivity to it's neighbours at time of placement, but the tick later
     */
    default boolean hasConnectionDelay() {
        return false;
    }
}
