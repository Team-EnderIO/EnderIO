package com.enderio.enderio.content.conduits.network.pathing;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Strategy interface for finding paths between conduit block connections.
 * <p>
 * This abstraction allows for different pathfinding algorithms to be implemented
 * and swapped as needed. Implementations should be stateless and thread-safe.
 * </p>
 * 
 * @apiNote The separation of pathfinding logic from network management enables
 *          independent testing and future algorithm optimizations without
 *          modifying the core network implementation.
 */
public interface ConduitPathingStrategy {

    /**
     * Find a path between two conduit block connections within a network.
     * <p>
     * The implementation should use the provided {@link PathfindingContext} to
     * traverse the network graph and compute path properties.
     * </p>
     *
     * @param from    the starting connection
     * @param to      the target connection
     * @param context provides read-only access to the network graph structure
     * @return an optional containing the path if one exists, or empty if no path connects the two points
     */
    Optional<ConduitConnectionPath> findPath(
        ConduitBlockConnection from,
        ConduitBlockConnection to,
        PathfindingContext context
    );

    /**
     * Find paths from one connection to many targets.
     * <p>
     * Implementations are encouraged to override this with a single graph traversal shared between all targets,
     * as calling {@link #findPath} once per target is expensive on large networks. Targets with no path are
     * absent from the result.
     * </p>
     *
     * @param from    the starting connection
     * @param targets the connections to find paths to
     * @param context provides read-only access to the network graph structure
     * @return the path to each reachable target
     */
    default Map<ConduitBlockConnection, ConduitConnectionPath> findPaths(
        ConduitBlockConnection from,
        Collection<ConduitBlockConnection> targets,
        PathfindingContext context
    ) {
        Map<ConduitBlockConnection, ConduitConnectionPath> result = new LinkedHashMap<>();
        for (var target : targets) {
            findPath(from, target, context).ifPresent(path -> result.put(target, path));
        }
        return result;
    }
}
