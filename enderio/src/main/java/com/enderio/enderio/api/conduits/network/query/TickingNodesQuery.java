package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.GraphRebuilt;
import com.enderio.enderio.api.conduits.network.NodeAdded;
import com.enderio.enderio.api.conduits.network.NodeRemoved;
import com.enderio.enderio.api.conduits.network.NodeUpdated;
import com.enderio.enderio.api.conduits.network.NodesLoaded;
import com.enderio.enderio.api.conduits.network.NodesUnloaded;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public class TickingNodesQuery implements ConduitNetworkQuery<Set<ConduitNode>> {

    public static final ConduitNetworkQueryType<TickingNodesQuery> TYPE = new ConduitNetworkQueryType<>(TickingNodesQuery::new, Set.of());

    private final Set<ConduitNode> tickingNodes = Sets.newHashSet();

    @Override
    public ConduitNetworkQueryType<?> type() {
        return TYPE;
    }

    @Override
    public Set<ConduitNode> query() {
        return Collections.unmodifiableSet(tickingNodes);
    }

    @Override
    public boolean processUpdates(ConduitNetwork network, Set<ConduitNetworkChange> networkChanges) {
        if (networkChanges.contains(GraphRebuilt.INSTANCE)) {
            rebuild(network);
            return true;
        }

        boolean didChange = false;
        for (ConduitNetworkChange networkChange : networkChanges) {
            switch (networkChange) {
            case GraphRebuilt ignored -> {}
            case NodeAdded nodeAdded -> {
                if (nodeAdded.node().isTicking()) {
                    didChange |= tickingNodes.add(nodeAdded.node());
                }
            }
            case NodeRemoved nodeRemoved -> {
                didChange |= tickingNodes.remove(nodeRemoved.node());
            }
            case NodeUpdated nodeUpdated -> {
                if (nodeUpdated.node().isTicking()) {
                    didChange |= tickingNodes.add(nodeUpdated.node());
                } else {
                    didChange |= tickingNodes.remove(nodeUpdated.node());
                }
            }
            case NodesLoaded nodesLoaded -> {
                for (ConduitNode node : nodesLoaded.nodes()) {
                    if (node.isTicking()) {
                        didChange |= tickingNodes.add(node);
                    }
                }
            }
            case NodesUnloaded nodesUnloaded -> {
                for (ConduitNode node : nodesUnloaded.nodes()) {
                    didChange |= tickingNodes.remove(node);
                }
            }
            }
        }

        return didChange;
    }

    private void rebuild(ConduitNetwork network) {
        tickingNodes.clear();

        for (ConduitNode node : network.nodes()) {
            if (node.isTicking()) {
                tickingNodes.add(node);
            }
        }
    }
}
