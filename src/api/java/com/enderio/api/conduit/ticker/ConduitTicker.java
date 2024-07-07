package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface ConduitTicker<TConduit extends Conduit<TConduit>> {

    void tickGraph(ServerLevel level, TConduit conduit, ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider);

    /**
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    default int getTickRate() {
        return 5;
    }

    /**
     * @return Whether the conduit can interact with the block in this direction
     */
    boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction);
}
