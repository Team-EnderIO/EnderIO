package com.enderio.enderio.conduits.tests.network.pathing;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.content.conduits.network.pathing.PathfindingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FakePathfindingContext implements PathfindingContext {
    private final Map<ConduitNode, Set<ConduitNode>> adjacencyList = new HashMap<>();
    private final Set<ConduitNode> allNodes = new HashSet<>();
    private int nodeCounter = 0;
    
    /**
     * Create a new fake node in this context.
     * @param id      debug identifier for the node
     * @param conduit the conduit holder for this node
     * @return the newly created fake node
     */
    public FakeConduitNode createNode(String id, Holder<Conduit<?, ?>> conduit) {
        BlockPos pos = BlockPos.ZERO.offset(nodeCounter++, 0, 0);
        FakeConduitNode node = new FakeConduitNode(id, pos, conduit);
        allNodes.add(node);
        adjacencyList.put(node, new HashSet<>());
        return node;
    }
    
    /**
     * Add a bidirectional edge between two nodes.
     * <p>
     * This simulates a conduit connection between the two nodes,
     * allowing pathfinding to traverse from either node to the other.
     * </p>
     * 
     * @param a first node
     * @param b second node
     */
    public void addEdge(ConduitNode a, ConduitNode b) {
        adjacencyList.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        adjacencyList.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }
    
    /**
     * Remove an edge between two nodes (if it exists).
     * 
     * @param a first node
     * @param b second node
     */
    public void removeEdge(ConduitNode a, ConduitNode b) {
        Set<ConduitNode> neighborsA = adjacencyList.get(a);
        if (neighborsA != null) {
            neighborsA.remove(b);
        }
        
        Set<ConduitNode> neighborsB = adjacencyList.get(b);
        if (neighborsB != null) {
            neighborsB.remove(a);
        }
    }
    
    @Override
    public Set<? extends ConduitNode> neighbors(ConduitNode node) {
        return adjacencyList.getOrDefault(node, Set.of());
    }
    
    @Override
    public Map<ConnectionPathProperty<?>, Object> collectNodeProperties(ConduitNode node) {
        if (node instanceof FakeConduitNode fake) {
            return fake.getPathProperties();
        }
        
        return Map.of();
    }
    
    @Override
    public boolean contains(ConduitNode node) {
        return allNodes.contains(node);
    }
    
    /**
     * Get the total number of nodes in this context.
     * 
     * @return node count
     */
    public int getNodeCount() {
        return allNodes.size();
    }
    
    /**
     * Get the total number of edges in this context.
     * 
     * @return edge count (each bidirectional edge counts as 1)
     */
    public int getEdgeCount() {
        return adjacencyList.values().stream()
            .mapToInt(Set::size)
            .sum() / 2; // Divide by 2 because edges are bidirectional
    }
}
