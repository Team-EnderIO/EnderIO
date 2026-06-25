package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public interface ConduitNetworkQuery<T> {
    ConduitNetworkQueryType<?> type();

    /**
     * @return the result of the query
     */
    T query();

    /**
     * Process changes in the network since the last time this query was accessed.
     * @apiNote Public so that the {@link ConduitNetwork} can call it, never update query caches yourself.
     * @param networkChanges the list of conduit network changes made.
     * @return whether changes were made to the cached query.
     */
    @ApiStatus.Internal
    boolean processUpdates(ConduitNetwork network, Set<ConduitNetworkChange> networkChanges);
}
