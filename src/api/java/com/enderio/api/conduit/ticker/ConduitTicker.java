package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.GraphAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface ConduitTicker<T extends ExtendedConduitData<T>> {

    void tickGraph(ServerLevel level, ConduitType<T> type, GraphAccessor<T> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);

    /**
     *
     * @return if this is not always able to determine connectivity to its neighbours at time of placement, but the tick later
     */
    default boolean hasConnectionDelay() {
        return false;
    }

    /**
     * @return true if both types are similar and share the same extended conduit data
     */
    default boolean canConnectTo(ConduitType<?> thisType, ConduitType<?> other) {
        return thisType == other;
    }
}
