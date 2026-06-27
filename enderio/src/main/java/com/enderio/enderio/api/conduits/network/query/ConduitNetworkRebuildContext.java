package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;

public interface ConduitNetworkRebuildContext {
    ConduitNetwork network();

    <T extends ConduitNetworkQuery<?>> T getDependency(ConduitNetworkQuery.Type<T> type);
}
