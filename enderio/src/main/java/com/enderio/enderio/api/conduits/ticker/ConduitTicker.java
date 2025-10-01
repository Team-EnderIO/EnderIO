package com.enderio.enderio.api.conduits.ticker;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import net.minecraft.server.level.ServerLevel;

public interface ConduitTicker<TConduit extends Conduit<TConduit, ?>> {
    void tick(ServerLevel level, TConduit conduit, ConduitNetwork network);
}
