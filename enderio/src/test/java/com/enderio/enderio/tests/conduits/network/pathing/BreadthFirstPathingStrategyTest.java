package com.enderio.enderio.tests.conduits.network.pathing;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.content.conduits.network.pathing.BreadthFirstPathingStrategy;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class BreadthFirstPathingStrategyTest {
    
    private Holder<Conduit<?, ?>> testConduit;
    private BreadthFirstPathingStrategy pathfinder;
    
    @BeforeEach
    void setup(MinecraftServer server) {
        // Get a real conduit holder from the registry
        var conduitsRegistry = server.registryAccess()
            .registryOrThrow(EnderIORegistries.Keys.CONDUIT);
        testConduit = conduitsRegistry.getHolderOrThrow(EIOConduits.ITEM);
        
        pathfinder = new BreadthFirstPathingStrategy();
    }
    
    @Test
    void testSimpleTwoNodePath() {
        // Arrange: A -> B
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        context.addEdge(nodeA, nodeB);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connB = new ConduitBlockConnection(nodeB, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connB, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist between connected nodes");
        Assertions.assertEquals(2, result.get().length(), "Path length should be 2 (A to B)");
        Assertions.assertEquals(connA, result.get().start(), "Path should start at connA");
        Assertions.assertEquals(connB, result.get().end(), "Path should end at connB");
    }
    
    @Test
    void testMultiHopPath() {
        // Arrange: A -> B -> C -> D
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        var nodeD = context.createNode("D", testConduit);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeB, nodeC);
        context.addEdge(nodeC, nodeD);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connD = new ConduitBlockConnection(nodeD, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connD, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist through intermediate nodes");
        Assertions.assertEquals(4, result.get().length(), "Path length should be 4 (A->B->C->D)");
    }
    
    @Test
    void testSameNodePath() {
        // Arrange: Same node, different connection sides
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        
        var connNorth = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connSouth = new ConduitBlockConnection(nodeA, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connNorth, connSouth, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist for same node");
        Assertions.assertEquals(1, result.get().length(), "Same node path length should be 1");
    }
    
    @Test
    void testSingleNodeGraph() {
        // Arrange: Single isolated node
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        
        var conn1 = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var conn2 = new ConduitBlockConnection(nodeA, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(conn1, conn2, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist on single node");
        Assertions.assertEquals(1, result.get().length());
    }
    
    @Test
    void testPropertyAggregation_MinInt() {
        // Arrange: Test that minInt property correctly finds minimum
        var speedProperty = ConnectionPathProperty.minInt(100);
        
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        
        nodeA.setPathProperty(speedProperty, 100);
        nodeB.setPathProperty(speedProperty, 50);   // Bottleneck
        nodeC.setPathProperty(speedProperty, 75);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeB, nodeC);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connC = new ConduitBlockConnection(nodeC, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connC, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist");
        Assertions.assertEquals(50, result.get().property(speedProperty), 
            "Min property should be 50 (bottleneck at node B)");
    }
    
    @Test
    void testPropertyAggregation_MaxInt() {
        // Arrange: Test that maxInt property correctly finds maximum
        var capacityProperty = ConnectionPathProperty.maxInt(0);
        
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        
        nodeA.setPathProperty(capacityProperty, 100);
        nodeB.setPathProperty(capacityProperty, 200);
        
        context.addEdge(nodeA, nodeB);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connB = new ConduitBlockConnection(nodeB, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connB, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist");
        Assertions.assertEquals(200, result.get().property(capacityProperty),
            "Max property should be 200");
    }
    
    @Test
    void testPropertyAggregation_SumInt() {
        // Arrange: Test that sumInt property correctly sums values
        var costProperty = ConnectionPathProperty.sumInt(0);
        
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        
        nodeA.setPathProperty(costProperty, 10);
        nodeB.setPathProperty(costProperty, 20);
        nodeC.setPathProperty(costProperty, 30);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeB, nodeC);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connC = new ConduitBlockConnection(nodeC, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connC, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist");
        Assertions.assertEquals(60, result.get().property(costProperty),
            "Sum property should be 60 (10+20+30)");
    }
    
    @Test
    void testCyclicGraph() {
        // Arrange: Create a cycle, ensure BFS still finds shortest path
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        var nodeD = context.createNode("D", testConduit);
        
        // Create a square: A-B-D-C-A
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeB, nodeD);
        context.addEdge(nodeD, nodeC);
        context.addEdge(nodeC, nodeA);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connD = new ConduitBlockConnection(nodeD, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connD, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist in cyclic graph");
        Assertions.assertEquals(3, result.get().length(), 
            "Shortest path should be 3 (A->B->D, not A->C->D)");
    }
    
    @Test
    void testComplexGraph_DiamondPattern() {
        // Arrange: Diamond pattern with equal length paths
        //     B
        //   /   \
        // A       D
        //   \   /
        //     C
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        var nodeD = context.createNode("D", testConduit);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeA, nodeC);
        context.addEdge(nodeB, nodeD);
        context.addEdge(nodeC, nodeD);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connD = new ConduitBlockConnection(nodeD, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connD, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist in diamond pattern");
        Assertions.assertEquals(3, result.get().length(),
            "Both paths are equal length (3), BFS should find one of them");
    }
    
    @Test
    void testComplexGraph_MultipleHubsAndSpokes() {
        // Arrange: More complex topology with hubs
        //   B - E
        //  /     \
        // A - C - F - G
        //  \     /
        //   D - H
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        var nodeD = context.createNode("D", testConduit);
        var nodeE = context.createNode("E", testConduit);
        var nodeF = context.createNode("F", testConduit);
        var nodeG = context.createNode("G", testConduit);
        var nodeH = context.createNode("H", testConduit);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeA, nodeC);
        context.addEdge(nodeA, nodeD);
        context.addEdge(nodeB, nodeE);
        context.addEdge(nodeC, nodeF);
        context.addEdge(nodeD, nodeH);
        context.addEdge(nodeE, nodeF);
        context.addEdge(nodeF, nodeG);
        context.addEdge(nodeH, nodeF);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connG = new ConduitBlockConnection(nodeG, Direction.SOUTH);
        
        // Act
        var result = pathfinder.findPath(connA, connG, context);
        
        // Assert
        Assertions.assertTrue(result.isPresent(), "Path should exist in complex graph");
        Assertions.assertEquals(4, result.get().length(),
            "Shortest path should be 4 (A->C->F->G)");
    }
    
    @Test
    void testPathReversibility() {
        // Arrange: Path from A to B should have same length as B to A
        var context = new FakePathfindingContext();
        var nodeA = context.createNode("A", testConduit);
        var nodeB = context.createNode("B", testConduit);
        var nodeC = context.createNode("C", testConduit);
        
        context.addEdge(nodeA, nodeB);
        context.addEdge(nodeB, nodeC);
        
        var connA = new ConduitBlockConnection(nodeA, Direction.NORTH);
        var connC = new ConduitBlockConnection(nodeC, Direction.SOUTH);
        
        // Act
        var pathAtoC = pathfinder.findPath(connA, connC, context);
        var pathCtoA = pathfinder.findPath(connC, connA, context);
        
        // Assert
        Assertions.assertTrue(pathAtoC.isPresent(), "Forward path should exist");
        Assertions.assertTrue(pathCtoA.isPresent(), "Reverse path should exist");
        Assertions.assertEquals(pathAtoC.get().length(), pathCtoA.get().length(),
            "Forward and reverse paths should have same length");
    }
}
