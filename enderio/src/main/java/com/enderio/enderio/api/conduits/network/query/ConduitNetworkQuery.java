package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;

public interface ConduitNetworkQuery<T> {
    ConduitNetworkQueryType<?> type();

    /**
     * Called when the network has changed so much, the cache should just be rebuilt.
     * @param network the network that changed.
     */
    // TODO: Pass in access to the already computed caches
    @ApiStatus.Internal
    void fullRebuild(ConduitNetwork network);

    /**
     * Process changes in the network since the last time this query was accessed.
     * @apiNote Public so that the {@link ConduitNetwork} can call it, never update query caches yourself.
     * @param networkChanges the list of conduit network changes made.
     * @return a payload describing the changes made to the cache. Can be as simple as a boolean or a pack of information for dependent nodes.
     */
    // TODO: Pass in an object that provides access to already computed caches *and their change states*
    @ApiStatus.Internal
    T processUpdates(ConduitNetwork network, List<ConduitNetworkChange> networkChanges);
}
