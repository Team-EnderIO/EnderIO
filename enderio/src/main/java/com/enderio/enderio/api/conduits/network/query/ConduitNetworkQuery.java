package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;

import java.util.Set;

public interface ConduitNetworkQuery {
    ConduitNetworkQueryType<?> type();

    /**
     * Process changes in the network since the last time this query was accessed.
     * @param networkChanges the list of conduit network changes made.
     * @return whether changes were made to the cached query.
     */
    boolean processUpdates(ConduitNetwork network, Set<ConduitNetworkChange> networkChanges);
}
