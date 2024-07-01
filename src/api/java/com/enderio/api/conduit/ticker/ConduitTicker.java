package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.Conduit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface ConduitTicker<TConduit extends Conduit<TConduit, TContext, TData>, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>> {

    void tickGraph(ServerLevel level, TConduit conduit, ConduitNetwork<TContext, TData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    // TODO: I'd argue this goes into ConduitType, and then you can use getTicker() if you need additional context from it.
    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);

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
    default boolean canConnectTo(Holder<Conduit<?, ?, ?>> thisType, Holder<Conduit<?, ?, ?>> other) {
        return thisType.equals(other);
    }
}
