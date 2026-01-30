package com.enderio.enderio.api.conduits.ticker;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.google.common.base.Preconditions;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class ConduitTickerBase<T extends Conduit<T, ?>> {

    public final void tick(ServerLevel level, ConduitNetwork network, int tickOffset) {
        // Only tick if we're supposed to.
        if ((level.getGameTime()) % tickRate() == tickOffset % tickRate()) {
            // Preconditions when we actually tick to save doing comparisons each tick.
            Preconditions.checkArgument(network.conduitType() == conduitType(), "Network is not of correct type");
            Preconditions.checkArgument(network.conduitType().ticker() == this, "Incorrect ticker for network's conduit type");

            // Pre-tick logic (refresh network caches etc).
            network.beforeTicking();

            // Now tick!
            tickNetwork(level, network, tickOffset);
        }
    }

    protected abstract ConduitType<T> conduitType();

    public abstract int tickRate();

    /**
     * Tick the network.
     * @param level the level to tick in
     * @param network the network to tick
     * @param tickOffset an offset to apply to tick checks if implementing variable tick rates.
     */
    protected abstract void tickNetwork(ServerLevel level, ConduitNetwork network, int tickOffset);
}
