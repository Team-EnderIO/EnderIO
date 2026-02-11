package com.enderio.enderio.content.conduits.network.pathing;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import org.jetbrains.annotations.ApiStatus;

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
}
