package com.enderio.enderio.content.conduits.network.pathing;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.collect.Maps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Standard breadth-first search pathfinding implementation for conduit networks.
 * <p>
 * This algorithm finds the shortest path (by hop count) between two conduit connections
 * while aggregating path properties from each node along the way.
 * </p>
 * <p>
 * The BFS guarantees finding the shortest path in terms of number of hops, making it
 * suitable for conduit networks where all connections have equal cost.
 * </p>
 */
public class BreadthFirstPathingStrategy implements ConduitPathingStrategy {
    
    @Override
    public Optional<ConduitConnectionPath> findPath(
        ConduitBlockConnection from,
        ConduitBlockConnection to,
        PathfindingContext context
    ) {
        return Optional.ofNullable(findPaths(from, List.of(to), context).get(to));
    }

    /**
     * Finds paths to every target with a single breadth-first search from the source.
     * <p>
     * The search stops as soon as every target node has been reached. It records how far each node is from the
     * source and which node discovered it, which is enough to rebuild every target path without searching again.
     * Paths are rebuilt inside this call so the parent map is not retained, and properties are aggregated lazily
     * only when a path's properties are first read, in path length rather than by traversing the network again.
     * </p>
     */
    @Override
    public Map<ConduitBlockConnection, ConduitConnectionPath> findPaths(
        ConduitBlockConnection from,
        Collection<ConduitBlockConnection> targets,
        PathfindingContext context
    ) {
        Map<ConduitBlockConnection, ConduitConnectionPath> results = new LinkedHashMap<>();
        if (targets.isEmpty()) {
            return results;
        }

        ConduitNode start = from.node();

        // Several connections can share one node, so search by node.
        Set<ConduitNode> targetNodes = new HashSet<>();
        for (var target : targets) {
            targetNodes.add(target.node());
        }

        int remainingNodes = targetNodes.size();

        // Number of nodes on the path from the start to each node (inclusive of both ends).
        Map<ConduitNode, Integer> length = new HashMap<>();
        // The node each node was discovered from, used to rebuild paths without searching again.
        Map<ConduitNode, ConduitNode> parent = new HashMap<>();
        Queue<ConduitNode> queue = new ArrayDeque<>();

        length.put(start, 1);
        parent.put(start, start);
        queue.add(start);

        // Special case: same node
        if (targetNodes.contains(start)) {
            remainingNodes--;
        }

        while (!queue.isEmpty() && remainingNodes > 0) {
            ConduitNode current = queue.poll();
            int neighborLength = length.get(current) + 1;

            for (ConduitNode neighbor : context.neighbors(current)) {
                if (length.putIfAbsent(neighbor, neighborLength) != null) {
                    continue;
                }

                parent.put(neighbor, current);

                if (targetNodes.contains(neighbor)) {
                    remainingNodes--;
                }

                queue.add(neighbor);
            }
        }

        // Preserve the order of the targets we were given, so results are deterministic.
        for (var target : targets) {
            var pathLength = length.get(target.node());

            // Unreachable targets were never discovered. BFS assigns the shortest length on discovery.
            if (pathLength != null) {
                // Rebuild the path now, while we are still inside the search budget, so the cached path does
                // not retain the whole parent map (and the nodes it references) until its properties are read.
                List<ConduitNode> path = reconstructPath(start, target.node(), parent);
                results.put(target, new ConduitConnectionPath(from, target, pathLength,
                    () -> collectPathProperties(path, context)));
            }
        }

        return results;
    }

    /**
     * Rebuild the node chain from the discovery parent map, in start -> goal order.
     */
    private List<ConduitNode> reconstructPath(ConduitNode start, ConduitNode goal,
        Map<ConduitNode, ConduitNode> parent) {
        if (!parent.containsKey(goal)) {
            return List.of();
        }

        // Rebuild path (goal -> start), then reverse into traversal order.
        ArrayList<ConduitNode> path = new ArrayList<>();
        ConduitNode node = goal;
        while (!node.equals(start)) {
            path.add(node);
            var next = parent.get(node);
            if (next == null) {
                return List.of();
            }
            node = next;
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    /**
     * Aggregate the properties of the nodes along an already rebuilt path.
     */
    private Map<ConnectionPathProperty<?>, Object> collectPathProperties(List<ConduitNode> path,
        PathfindingContext context) {
        if (path.isEmpty()) {
            // There is no path to aggregate.
            return Map.of();
        }

        Map<ConnectionPathProperty<?>, List<Object>> aggregates = Maps.newHashMap();
        for (ConduitNode pathNode : path) {
            mergePropertyMap(aggregates, context.collectNodeProperties(pathNode));
        }

        return aggregatePropertyValues(aggregates);
    }
    
    // region Property Aggregation Helpers
    
    /**
     * Merge new property values into an existing aggregate map.
     * Appends each new value to the corresponding property's list.
     */
    private void mergePropertyMap(
        Map<ConnectionPathProperty<?>, List<Object>> target,
        Map<ConnectionPathProperty<?>, Object> additions
    ) {
        additions.forEach((property, value) -> 
            target.computeIfAbsent(property, k -> new ArrayList<>()).add(value)
        );
    }
    
    /**
     * Aggregate all collected property values into their final forms.
     * Uses each property's aggregation function (e.g., min, max, sum).
     */
    private Map<ConnectionPathProperty<?>, Object> aggregatePropertyValues(
        Map<ConnectionPathProperty<?>, List<Object>> aggregatedLists
    ) {
        Map<ConnectionPathProperty<?>, Object> aggregatedValues = Maps.newHashMap();
        aggregatedLists.forEach((property, values) -> 
            aggregatedValues.put(property, aggregateProperty(property, values))
        );
        return aggregatedValues;
    }
    
    /**
     * Apply a property's aggregation function to a list of values.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object aggregateProperty(ConnectionPathProperty property, List<Object> values) {
        return property.aggregate(values);
    }
    
    // endregion
}
