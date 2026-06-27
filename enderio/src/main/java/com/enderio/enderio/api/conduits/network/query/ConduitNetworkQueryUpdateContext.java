package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;

import java.util.List;

public interface ConduitNetworkQueryUpdateContext {
    ConduitNetwork network();

    List<ConduitNetworkChange> changes();

    <T extends ConduitNetworkQuery<?>> T getDependency(ConduitNetworkQuery.Type<T> type);

    <T extends ConduitNetworkQuery<U>, U> U getDependencyChanges(ConduitNetworkQuery.Type<T> type);
}
