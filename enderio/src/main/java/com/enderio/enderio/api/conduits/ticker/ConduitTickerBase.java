package com.enderio.enderio.api.conduits.ticker;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
public abstract class ConduitTickerBase<T extends Conduit<T, ?>> {

    public final void tick(ServerLevel level, ConduitNetwork network, int tickOffset) {
        // See if we can tick any conduits in this network
        List<Holder<Conduit<?, ?>>> tickableConduits = network.getTickableConduits(level.getGameTime(), tickOffset);

        // Only tick if we're supposed to.
        if (tickableConduits.isEmpty()) {
            return;
        }

        // Preconditions when we actually tick to save doing comparisons each tick.
        Preconditions.checkArgument(network.conduitType() == conduitType(), "Network is not of correct type");
        Preconditions.checkArgument(network.conduitType().ticker() == this, "Incorrect ticker for network's conduit type");

        // Pre-tick logic (refresh network caches etc).
        network.beforeTicking();

        // Now tick!
        tickNetwork(level, network, tickableConduits);
    }

    protected abstract ConduitType<T> conduitType();

    /**
     * Tick the network.
     * @param level the level to tick in
     * @param network the network to tick
     * @param tickableConduits a list of conduits allowed to tick. Can be ignored if you know for sure all conduits tick at the same rate.
     */
    protected abstract void tickNetwork(ServerLevel level, ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits);
}
