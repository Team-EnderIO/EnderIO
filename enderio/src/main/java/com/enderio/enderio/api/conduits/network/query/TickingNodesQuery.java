package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.NodeAdded;
import com.enderio.enderio.api.conduits.network.NodeRemoved;
import com.enderio.enderio.api.conduits.network.NodeUpdated;
import com.enderio.enderio.api.conduits.network.NodesLoaded;
import com.enderio.enderio.api.conduits.network.NodesUnloaded;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TickingNodesQuery implements ConduitNetworkQuery<TickingNodesQuery.UpdateResult> {

    public static final Type<TickingNodesQuery> TYPE = new Type<>(TickingNodesQuery::new, Set.of());

    private final Set<ConduitNode> tickingNodes = Sets.newHashSet();

    @Override
    public Type<?> type() {
        return TYPE;
    }

    public Set<ConduitNode> tickingNodes() {
        return Collections.unmodifiableSet(tickingNodes);
    }

    @Override
    public void fullRebuild(ConduitNetworkRebuildContext context) {
        tickingNodes.clear();
        for (ConduitNode node : context.network().nodes()) {
            if (node.isTicking()) {
                tickingNodes.add(node);
            }
        }
    }

    @Override
    public UpdateResult processUpdates(ConduitNetworkQueryUpdateContext context) {
        Set<ConduitNode> addedNodes = new HashSet<>();
        Set<ConduitNode> removedNodes = new HashSet<>();

        for (ConduitNetworkChange networkChange : context.changes()) {
            switch (networkChange) {
            case NodeAdded nodeAdded -> {
                if (nodeAdded.node().isTicking()) {
                    if (tickingNodes.add(nodeAdded.node())) {
                        addedNodes.add(nodeAdded.node());
                    }
                }
            }
            case NodeRemoved nodeRemoved -> {
                if (tickingNodes.remove(nodeRemoved.node())) {
                    removedNodes.add(nodeRemoved.node());
                }
            }
            case NodeUpdated nodeUpdated -> {
                if (nodeUpdated.node().isTicking()) {
                    if (tickingNodes.add(nodeUpdated.node())) {
                        addedNodes.add(nodeUpdated.node());
                    }
                } else {
                    if (tickingNodes.remove(nodeUpdated.node())) {
                        removedNodes.add(nodeUpdated.node());
                    }
                }
            }
            case NodesLoaded nodesLoaded -> {
                for (ConduitNode node : nodesLoaded.nodes()) {
                    if (node.isTicking()) {
                        if (tickingNodes.add(node)) {
                            addedNodes.add(node);
                        }
                    }
                }
            }
            case NodesUnloaded nodesUnloaded -> {
                for (ConduitNode node : nodesUnloaded.nodes()) {
                    if (tickingNodes.remove(node)) {
                        removedNodes.add(node);
                    }
                }
            }
            }
        }

        // Normalize, if a node was removed and added again ignore its removal
        removedNodes.removeAll(addedNodes);
        return new UpdateResult(addedNodes, removedNodes);
    }

    public record UpdateResult(Set<ConduitNode> addedNodes, Set<ConduitNode> removedNodes) {
        public static final UpdateResult EMPTY = new UpdateResult(Set.of(), Set.of());

        public boolean didChange() {
            return !addedNodes.isEmpty() || !removedNodes.isEmpty();
        }
    }
}
