package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface ConduitTicker<T extends ConduitData<T>> {

    void tickGraph(ServerLevel level, ConduitType<T> type, ConduitGraph<T> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    // TODO: I'd argue this goes into ConduitType, and then you can use getTicker() if you need additional context from it.
    /**
     * @return if the conduit can automatically connect to a neighbor block
     */
    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);

    /**
     * @return if the conduit is allowed to have a forced connection (with the wrench) but won't necessarily connect when placed
     */
    default boolean canForceConnect(Level level, BlockPos conduitPos, Direction direction) {
        return canConnectTo(level, conduitPos, direction);
    }

    /**
     *
     * @return if this is not always able to determine connectivity to its neighbours at time of placement, but the tick later
     */
    // TODO: Also belongs in ConduitType imo.
    default boolean hasConnectionDelay() {
        return false;
    }

    /**
     * @return true if both types are similar and share the same extended conduit data
     */
    // TODO: This should be in ConduitType too.
    default boolean canConnectTo(ConduitType<?> thisType, ConduitType<?> other) {
        return thisType == other;
    }
}
