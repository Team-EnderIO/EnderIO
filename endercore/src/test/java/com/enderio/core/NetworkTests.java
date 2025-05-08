package com.enderio.core;

import com.enderio.core.common.graph.BasicNetwork;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkTests {

    @Test
    public void testBasicNetwork() {
        var node1 = new TestNode();
        var node2 = new TestNode();

        BasicNetwork<TestNode> network = new BasicNetwork<>(node1);

        Assertions.assertNotNull(node1.getNetwork());
        Assertions.assertEquals(node1.getNetwork(), network);

        Assertions.assertDoesNotThrow(() -> network.connect(node1, node2));
        Assertions.assertNotNull(node2.getNetwork());
        Assertions.assertEquals(node2.getNetwork(), network);
    }

    @Test
    public void testNetworkMerging() {
        // Create nodes
        var node1 = new TestNode();
        var node2 = new TestNode();
        var node3 = new TestNode();
        var node4 = new TestNode();

        // Create two separate networks
        var network1 = new BasicNetwork<>(node1);
        var network2 = new BasicNetwork<>(node3);

        // Connect their secondary nodes
        network1.connect(node1, node2);
        network2.connect(node3, node4);

        // Merge the networks
        // We expect network 1 to be discarded because we're merging a member of network
        // 1 into network 2.
        AtomicBoolean wasNetwork1Discarded = new AtomicBoolean(false);
        Assertions
                .assertDoesNotThrow(() -> network2.connect(node3, node2, n -> wasNetwork1Discarded.set(n == network1)));
        Assertions.assertTrue(wasNetwork1Discarded.get(), "Network 1 was not passed via the discard callback.");

        // Ensure networks all match now
        Assertions.assertEquals(node1.getNetwork(), node3.getNetwork(),
                "Node 1 and Node 3 do not share the same network after merge.");
        Assertions.assertEquals(node2.getNetwork(), node4.getNetwork(),
                "Node 2 and Node 4 do not share the same network after merge.");

        // Ensure discard works as expected
        Assertions.assertTrue(network1.isDiscarded(), "Network 1 does not report as discarded.");
        Assertions.assertThrows(IllegalStateException.class, () -> network1.contains(node3),
                "Discarded network does not throw an exception when trying to access it.");
        Assertions.assertTrue(network2.contains(node3), "Network 2 does not contain the newly added node 3.");
    }

    @Test
    public void testNetworkSplitting() {
        // Create nodes for the network
        var node1 = new TestNode();
        var node2 = new TestNode();
        var node3 = new TestNode();

        // Create the initial network
        var network = new BasicNetwork<>(node1);

        // Connect 1 -> 2 and 2 -> 3.
        network.connect(node1, node2);
        network.connect(node2, node3);

        // Ensure all nodes belong to the same graph
        Assertions.assertEquals(node1.getNetwork(), node3.getNetwork());

        // Remove the connection between node2 and node3
        Assertions.assertDoesNotThrow(() -> node2.getNetwork().remove(node2));

        // Verify graph split
        Assertions.assertNotEquals(node1.getNetwork(), node3.getNetwork());
    }

    @Test
    public void testIntegrateSingleGraph() {
//        var node1 = new TestNode();
//        var neighbor1 = new TestNode();
//        var neighbor2 = new TestNode();
//
//        // Connect initial neighbors
//        Graph.connect(neighbor1, neighbor2);
//
//        // Integrate node1 with neighbors in the same graph
//        Assertions.assertDoesNotThrow(() -> Graph.integrate(node1, List.of(neighbor1, neighbor2)));
//
//        // Verify all nodes are part of the same graph
//        Assertions.assertNotNull(node1.getGraph());
//        Assertions.assertEquals(node1.getGraph(), neighbor1.getGraph());
//        Assertions.assertEquals(node1.getGraph(), neighbor2.getGraph());
    }

    @Test
    public void testIntegrateWithNoNeighbors() {
//        var node1 = new TestNode();
//
//        // Integrate node1 with no neighbors
//        Assertions.assertDoesNotThrow(() -> Graph.integrate(node1, List.of()));
//
//        // Verify node1 is in a graph
//        Assertions.assertNotNull(node1.getGraph());
//        Assertions.assertEquals(1, node1.getGraph().getNodes().size());
//        Assertions.assertTrue(node1.getGraph().contains(node1));
    }

    @Test
    public void testIntegrateMergingMultipleGraphs() {
//        var node1 = new TestNode();
//        var neighbor1 = new TestNode();
//        var neighbor2 = new TestNode();
//        var neighbor3 = new TestNode();
//
//        // Create two separate graphs
//        Graph.connect(neighbor1, neighbor2);
//        Graph.integrate(neighbor3, List.of()); // neighbor3 in a separate graph
//
//        // Integrate node1 with neighbors from multiple graphs
//        Assertions.assertDoesNotThrow(() -> Graph.integrate(node1, List.of(neighbor1, neighbor3)));
//
//        // Verify all nodes are part of the same graph
//        Assertions.assertNotNull(node1.getGraph());
//        Assertions.assertEquals(node1.getGraph(), neighbor1.getGraph());
//        Assertions.assertEquals(node1.getGraph(), neighbor3.getGraph());
    }

    @Test
    public void testIntegrateWithAlreadyExistingGraph() {
//        var node1 = new TestNode();
//        var node2 = new TestNode();
//
//        // Connect initial nodes
//        Graph.connect(node1, node2);
//
//        // Attempt to integrate a node that already belongs to a graph
//        Assertions.assertThrows(IllegalArgumentException.class, () -> Graph.integrate(node1, List.of(node2)));
    }

}
