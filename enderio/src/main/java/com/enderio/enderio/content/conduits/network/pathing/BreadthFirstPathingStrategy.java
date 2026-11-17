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
     * The search stops as soon as every target node has been reached. It only records how far each node is from
     * the source, which gives each path's length. Path properties are aggregated lazily, only when a path's
     * properties are first read, as aggregating them along every path is far more expensive than the search
     * itself on large networks.
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
        Queue<ConduitNode> queue = new ArrayDeque<>();

        length.put(start, 1);
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
                results.put(target, new ConduitConnectionPath(from, target, pathLength,
                    () -> collectPathProperties(start, target.node(), context)));
            }
        }

        return results;
    }

    /**
     * Find the shortest path between two nodes and aggregate the properties of the nodes along it.
     */
    private Map<ConnectionPathProperty<?>, Object> collectPathProperties(
        ConduitNode start,
        ConduitNode goal,
        PathfindingContext context
    ) {
        Map<ConduitNode, ConduitNode> parent = new HashMap<>();
        Queue<ConduitNode> queue = new ArrayDeque<>();

        parent.put(start, start);
        queue.add(start);

        while (!queue.isEmpty() && !parent.containsKey(goal)) {
            ConduitNode current = queue.poll();

            for (ConduitNode neighbor : context.neighbors(current)) {
                if (parent.putIfAbsent(neighbor, current) == null) {
                    queue.add(neighbor);
                }
            }
        }

        if (!parent.containsKey(goal)) {
            // The network changed since the path was found, there is nothing to aggregate.
            return Map.of();
        }

        // Rebuild path (goal -> start), then aggregate in start -> goal order.
        ArrayList<ConduitNode> path = new ArrayList<>();
        ConduitNode node = goal;
        while (!node.equals(start)) {
            path.add(node);
            node = parent.get(node);
        }
        path.add(start);
        Collections.reverse(path);

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
