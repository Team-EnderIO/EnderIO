package com.enderio.enderio.content.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQueries;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQuery;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQueryType;
import com.enderio.enderio.content.conduits.network.ConduitNetworkImpl;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConduitNetworkQueryEngine {
    // Controls how many updates we'll store in memory for a 'patch' to caches before just rebuilding.
    // TODO: might expose as a config.
    private static final int MAX_CHANGES_BEFORE_REBUILD = 50;

    private final ConduitNetworkImpl network;
    private final List<ConduitNetworkChange> networkChangesSinceLastQuery;
    private final Set<ConduitNetworkQuery<?>> queries = new HashSet<>();

    private boolean isQuerying;
    private boolean shouldFullyRebuildCaches = true;

    public ConduitNetworkQueryEngine(ConduitNetworkImpl network) {
        this.network = network;
        this.networkChangesSinceLastQuery = new ArrayList<>(MAX_CHANGES_BEFORE_REBUILD);

        for (ConduitNetworkQueryType<?> queryType : network.conduitType().requiredQueryTypes()) {
            queries.add(queryType.create());
        }
    }

    private void addUpdate(ConduitNetworkChange change) {
        // If we're fully rebuilding, we don't track events
        if (shouldFullyRebuildCaches) {
            return;
        }

        // If adding another event would surpass our maximum size, we'll just recreate
        if (networkChangesSinceLastQuery.size() + 1 >= MAX_CHANGES_BEFORE_REBUILD) {
            shouldFullyRebuildCaches = true;
            networkChangesSinceLastQuery.clear();
            return;
        }

        networkChangesSinceLastQuery.add(change);
    }

    public ConduitNetworkQueries beginQuerying() {
        Preconditions.checkState(!isQuerying, "Cannot start querying until previous query session has completed.");

        // TODO: need to order such that dependent queries are updated first.

        if (shouldFullyRebuildCaches) {
            shouldFullyRebuildCaches = false;
            for (ConduitNetworkQuery<?> query : queries) {
                query.fullRebuild(network);
            }
        } else {
            List<ConduitNetworkChange> readOnlyChangeSet = Collections.unmodifiableList(networkChangesSinceLastQuery);
            for (ConduitNetworkQuery<?> query : queries) {
                query.processUpdates(network, readOnlyChangeSet);
            }
        }

        networkChangesSinceLastQuery.clear();

        isQuerying = true;
        return new ConduitNetworkQueriesImpl();
    }

    private class ConduitNetworkQueriesImpl implements ConduitNetworkQueries {

        private boolean isClosed;

        @Override
        public void close() throws Exception {
            Preconditions.checkState(!isClosed, "Already closed.");

            isQuerying = false;
            isClosed = true;
        }
    }
}
