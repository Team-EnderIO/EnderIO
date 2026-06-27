package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.NodeUpdated;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.Direction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockConnectionsQuery implements ConduitNetworkQuery<BlockConnectionsQuery.UpdateResult> {

    public static final Type<BlockConnectionsQuery> TYPE = new Type<>(BlockConnectionsQuery::new, Set.of(TickingNodesQuery.TYPE));

    private final SetMultimap<ConduitNode, ConduitBlockConnection> endpoints = HashMultimap.create();

    public Collection<ConduitBlockConnection> allConnections() {
        return Collections.unmodifiableCollection(endpoints.values());
    }

    public Set<ConduitBlockConnection> connectionsFor(ConduitNode node) {
        return Collections.unmodifiableSet(endpoints.get(node));
    }

    @Override
    public Type<?> type() {
        return TYPE;
    }

    @Override
    public void fullRebuild(ConduitNetworkRebuildContext context) {
        // Get list of ticking nodes and add their connections.
        TickingNodesQuery query = context.getDependency(TickingNodesQuery.TYPE);
        for (ConduitNode node : query.tickingNodes()) {
            addNode(node);
        }
    }

    @Override
    public UpdateResult processUpdates(ConduitNetworkQueryUpdateContext context) {
        TickingNodesQuery.UpdateResult tickingNodeChanges = context.getDependencyChanges(TickingNodesQuery.TYPE);

        Set<ConduitBlockConnection> removedConnections = new HashSet<>();
        Set<ConduitBlockConnection> addedConnections = new HashSet<>();

        // Remove all removed nodes
        for (ConduitNode node : tickingNodeChanges.removedNodes()) {
            removedConnections.addAll(endpoints.removeAll(node));
        }

        // Add all new nodes
        for (ConduitNode node : tickingNodeChanges.addedNodes()) {
            // Ensure there are no nodes left over from a stale state (just in case)
            removedConnections.addAll(endpoints.removeAll(node));

            // Now add the new connections
            addedConnections.addAll(addNode(node));
        }

        // Also handle any updated nodes to react to changes in connection state.
        for (ConduitNetworkChange change : context.changes()) {
            if (!(change instanceof NodeUpdated(ConduitNode node))) {
                continue;
            }

            List<Direction> currentlyConnectedSides = endpoints.get(node).stream().map(ConduitBlockConnection::connectionSide).toList();

            for (Direction side : Direction.values()) {
                var connection = new ConduitBlockConnection(node, side);
                boolean isConnected = node.isConnectedToBlock(side);

                if (isConnected && !currentlyConnectedSides.contains(side)) {
                    endpoints.put(node, connection);
                    addedConnections.add(connection);
                } else if (!isConnected && currentlyConnectedSides.contains(side)) {
                    endpoints.remove(node, connection);
                    removedConnections.add(connection);
                }
            }
        }

        // Normalize the sets of connections. Anything added shouldn't be in the removed set
        removedConnections.removeAll(addedConnections);
        return new UpdateResult(addedConnections, removedConnections);
    }

    private Set<ConduitBlockConnection> addNode(ConduitNode node) {
        for (Direction side : Direction.values()) {
            if (!node.isConnectedToBlock(side)) {
                continue;
            }

            ConduitBlockConnection connection = new ConduitBlockConnection(node, side);
            endpoints.put(node, connection);
        }

        return endpoints.get(node);
    }

    public record UpdateResult(Set<ConduitBlockConnection> addedConnections, Set<ConduitBlockConnection> removedConnections)
        implements ConduitNetworkCacheUpdateResult {

        public static final UpdateResult EMPTY = new UpdateResult(Set.of(), Set.of());

        @Override
        public boolean didChange() {
            return !addedConnections.isEmpty() || !removedConnections.isEmpty();
        }
    }
}
