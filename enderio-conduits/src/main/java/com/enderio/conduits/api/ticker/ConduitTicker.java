package com.enderio.conduits.api.ticker;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.network.IConduitNetwork;
import net.minecraft.server.level.ServerLevel;

public interface ConduitTicker<TConduit extends Conduit<TConduit, ?>> {
    void tick(ServerLevel level, TConduit conduit, IConduitNetwork network);
}
