package com.enderio.enderio.content.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.GraphRebuilt;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQueries;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQuery;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQueryType;
import com.enderio.enderio.content.conduits.network.ConduitNetworkImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConduitNetworkQueryEngine {
    // Controls how many updates we'll store in memory for a 'patch' to caches before just rebuilding.
    // TODO: might expose as a config.
    private static final int MAX_CHANGES_BEFORE_REBUILD = 50;

    private final ConduitNetworkImpl network;
    private final Set<ConduitNetworkChange> networkChangesSinceLastQuery;
    private final Set<ConduitNetworkQuery> queries = new HashSet<>();

    private boolean isQuerying;

    public ConduitNetworkQueryEngine(ConduitNetworkImpl network) {
        this.network = network;
        this.networkChangesSinceLastQuery = new HashSet<>(MAX_CHANGES_BEFORE_REBUILD);

        for (ConduitNetworkQueryType<?> queryType : network.conduitType().requiredQueryTypes()) {
            queries.add(queryType.factory().get());
        }
    }

    private void addUpdate(ConduitNetworkChange change) {
        // If we have a rebuild event, we don't add anything more.
        if (networkChangesSinceLastQuery.contains(GraphRebuilt.INSTANCE)) {
            return;
        }

        // If adding another event would surpass our maximum size, we'll just recreate
        if (networkChangesSinceLastQuery.size() + 1 >= MAX_CHANGES_BEFORE_REBUILD) {
            networkChangesSinceLastQuery.clear();
            networkChangesSinceLastQuery.add(GraphRebuilt.INSTANCE);
            return;
        }

        networkChangesSinceLastQuery.add(change);
    }

    public ConduitNetworkQueries beginQuerying() {
        Preconditions.checkState(!isQuerying, "Cannot start querying until previous query session has completed.");

        Set<ConduitNetworkChange> readOnlyChangeSet = Collections.unmodifiableSet(networkChangesSinceLastQuery);

        // TODO: need to order such that dependent queries are updated first.
        for (ConduitNetworkQuery query : queries) {
            query.processUpdates(network, readOnlyChangeSet);
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
