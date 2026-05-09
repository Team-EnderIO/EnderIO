package com.enderio.enderio.content.conduits.network.cache;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.connection.path.ConduitPath;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.content.conduits.network.ConduitNetworkImpl;
import com.enderio.enderio.content.conduits.network.pathing.BreadthFirstPathingStrategy;
import com.enderio.enderio.content.conduits.network.pathing.ConduitPathingStrategy;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Connections cache for bi-directional node connections (non IO)
 */
@SuppressWarnings("UnstableApiUsage")
public class ConnectionNodeCache implements ConduitNetworkCache {

    private final ConduitPathingStrategy pathfinder = new BreadthFirstPathingStrategy();

    // TODO: element order based on the conduit type.
    private final MutableValueGraph<ConduitBlockConnection, ConduitPath> connectionGraph = ValueGraphBuilder
        .undirected()
        .nodeOrder(ElementOrder.stable())
        .allowsSelfLoops(true)
        .build();

    private final ConduitNetworkImpl network;

    // TODO: Caffeine is apparently the replacement for this.
    private Cache<ConduitBlockConnection, List<ConduitConnectionPath>> pathByConnectionCache = CacheBuilder
        .newBuilder()
        .build();

    private boolean doEdgesNeedRebuilding;

    public ConnectionNodeCache(ConduitNetworkImpl network) {
        this.network = network;
    }

    @Override
    public void update(ConduitNode node, NetworkUpdateType type) {
        // If we're removing the node *or* it is now unloaded, take it out of the graph.
        if (type == NetworkUpdateType.NODE_REMOVED || !node.isLoaded()) {
            for (Direction side : Direction.values()) {
                connectionGraph.removeNode(new ConduitBlockConnection(node, side));
            }

            // Even though removing the node has broken edges between others, it may have increased path lengths for others
            doEdgesNeedRebuilding = true;
            return;
        }

        // Node has been added or updated, so lets make sure we have the relevant nodes.
        List<ConduitBlockConnection> addedNodes = new ArrayList<>(Direction.values().length);
        for (Direction side : Direction.values()) {
            var connection = new ConduitBlockConnection(node, side);

            if (node.isConnectedToBlock(side)) {
                boolean addedConnection = connectionGraph.addNode(connection);
                if (addedConnection) {
                    addedNodes.add(connection);
                }
            } else {
                connectionGraph.removeNode(connection);
            }
        }

        // If the node is new, rebuild edges to ensure that any shortened paths are discovered.
        if (type == NetworkUpdateType.NODE_ADDED) {
            doEdgesNeedRebuilding = true;
            return;
        }

        if (addedNodes.isEmpty() || doEdgesNeedRebuilding) {
            return;
        }

        for (var existingNode : connectionGraph.nodes()) {
            if (addedNodes.contains(existingNode)) {
                continue;
            }

            for (var newNode : addedNodes) {
                var path = pathfinder.findPath(existingNode, newNode, network);
                path.ifPresent(p -> connectionGraph.putEdgeValue(existingNode, newNode, new ConduitPath(p.length(), p.properties())));
            }
        }
    }

    @Override
    public void rebuild() {
        var nodes = connectionGraph.nodes().stream().toList();
        for (var node : nodes) {
            connectionGraph.removeNode(node);
        }

        for (var node : network.nodes()) {
            if (node.isLoaded()) {
                for (var side : Direction.values()) {
                    connectionGraph.addNode(new ConduitBlockConnection(node, side));
                }
            }
        }

        doEdgesNeedRebuilding = true;
    }

    public List<ConduitBlockConnection> allConnections() {
        var stream = connectionGraph.nodes()
            .stream()
            .filter(connection -> connection.node().isLoaded());

        var comparator = network.conduitType().connectionComparator();
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }

        return stream.toList();
    }

    public List<ConduitConnectionPath> allPathsFrom(ConduitBlockConnection connection) {
        ensureEdgesBuilt();

        // Attempt to retrieve cached paths
        // TODO: Consider still filtering for isLoaded even while this is cached.
//        var paths = pathByConnectionCache.getIfPresent(connection);
//        if (paths != null) {
//            return paths;
//        }

        var paths = connectionGraph.adjacentNodes(connection)
            .stream()
            .map(to -> resolvePath(connection, to))
            .flatMap(Optional::stream)
            .filter(path -> path.start().node().isLoaded() && path.end().node().isLoaded())
            .sorted(network.conduitType().connectionPathComparator())
            .toList();

//        pathByConnectionCache.put(connection, paths);
        return paths;
    }

    private Optional<ConduitConnectionPath> resolvePath(ConduitBlockConnection from, ConduitBlockConnection to) {
        var edge = connectionGraph.edgeValue(from, to);
        return edge.map(conduitPath -> new ConduitConnectionPath(from, to, conduitPath.length(), conduitPath.properties()));
    }

    private void ensureEdgesBuilt() {
        if (!doEdgesNeedRebuilding) {
            return;
        }

        // Remove all edges
        var edges = connectionGraph.edges().stream().toList();
        for (var edge : edges) {
            connectionGraph.removeEdge(edge.nodeU(), edge.nodeV());
        }

        // Rebuild all edges
        for (var firstNode : connectionGraph.nodes()) {
            for (var secondNode : connectionGraph.nodes()) {
                var path = pathfinder.findPath(firstNode, secondNode, (ConduitNetworkImpl)network);
                path.ifPresent(p -> connectionGraph.putEdgeValue(firstNode, secondNode, new ConduitPath(p.length(), p.properties())));
            }
        }

        pathByConnectionCache.invalidateAll();
    }
}
