package com.enderio.enderio.content.conduits.network.pathing;

import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Set;

/**
 * Provides read-only access to a conduit network's graph structure for pathfinding.
 * @apiNote Implementing this interface allows pathfinding algorithms to be tested
 *          independently using mock contexts, without requiring full network infrastructure.
 */
public interface PathfindingContext {
    
    /**
     * Get all neighboring nodes connected to the given node in the network graph.
     * @param node the node to query neighbors for
     * @return an immutable set of neighboring nodes, or an empty set if the node has no neighbors
     */
    Set<? extends ConduitNode> neighbors(ConduitNode node);

    /**
     * Collect path properties from a specific node.
     * @param node the node to collect properties from
     * @return a map of property types to their values for this node, or an empty map if no properties exist
     */
    Map<ConnectionPathProperty<?>, Object> collectNodeProperties(ConduitNode node);
}
