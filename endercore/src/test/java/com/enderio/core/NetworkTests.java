package com.enderio.core;

import com.enderio.core.common.graph.BasicNetwork;
import com.enderio.core.common.graph.Network;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkTests {

    @Test
    public void testBasicNetwork() {
        var node1 = new TestNode();
        var node2 = new TestNode();

        // Ensure the nodes have networks
        Assertions.assertTrue(node1.isValid());
        Assertions.assertTrue(node2.isValid());

        var network = node1.getNetwork();

        // Try to connect two nodes
        AtomicBoolean networkWasDiscarded = new AtomicBoolean(false);
        Assertions.assertDoesNotThrow(() -> network.connect(node1, node2, n -> networkWasDiscarded.set(true)));
        Assertions.assertTrue(networkWasDiscarded.get(), "Node 2's network was not discarded.");
        Assertions.assertEquals(node2.getNetwork(), network);
    }

    @Test
    public void testNetworkMerging() {
        // Create nodes
        var node1 = new TestNode();
        var node2 = new TestNode(false);
        var node3 = new TestNode();
        var node4 = new TestNode(false);

        // Ensure only node 1 and node 3 have networks
        Assertions.assertTrue(node1.isValid());
        Assertions.assertFalse(node2.isValid());
        Assertions.assertTrue(node3.isValid());
        Assertions.assertFalse(node4.isValid());

        // Designate two separate networks
        var network1 = node1.getNetwork();
        var network2 = node3.getNetwork();

        // Connect their secondary nodes (this will discard node2's and node4's
        // networks)
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
        var node2 = new TestNode(false);
        var node3 = new TestNode(false);

        // Get the main network
        var network = node1.getNetwork();

        // Connect 1 -> 2 and 2 -> 3.
        network.connect(node1, node2);
        network.connect(node2, node3);

        // Ensure all nodes belong to the same graph
        Assertions.assertEquals(node1.getNetwork(), node3.getNetwork());

        // Remove node2 from the network (should cause a split for node1 and node3, and
        // should invalidate node2)
        Assertions.assertDoesNotThrow(() -> node2.getNetwork().remove(node2));
        Assertions.assertFalse(node2.isValid());

        // Verify graph split
        Assertions.assertNotEquals(node1.getNetwork(), node3.getNetwork());
        Assertions.assertTrue(node1.getNetwork().contains(node1));
        Assertions.assertTrue(node3.getNetwork().contains(node3));
    }

    @Test
    void testNetworkConnectMany() {
        var node1 = new TestNode();

        // Create some neighbors
        var neighbors = new ArrayList<TestNode>();
        for (int i = 0; i < 4; i++) {
            neighbors.add(new TestNode(false));
        }

        // Get the network
        var network = node1.getNetwork();

        // Connect all the neighbors to node1.
        Assertions.assertDoesNotThrow(() -> network.connectMany(node1, neighbors));

        // Ensure all neighbors are now in this network
        neighbors.forEach(n -> Assertions.assertTrue(network.contains(n)));

        // Ensure all neighbors are connected to node1
        neighbors.forEach(n -> Assertions.assertTrue(network.neighbors(node1).contains(n)));
    }

    @Test
    public void testNetworkConstructionWithCyclicEdges() {
        var node1 = new TestNode(false);
        var node2 = new TestNode(false);
        var node3 = new TestNode(false);
        var node4 = new TestNode(false);

        var nodes = List.of(node1, node2, node3, node4);

        var edges = List.of(Pair.of(node1, node2), Pair.of(node2, node3), Pair.of(node3, node4));

        var network = new BasicNetwork<>(nodes, edges);

        // Ensure all networks are correct.
        nodes.forEach(n -> Assertions.assertEquals(network, n.getNetwork()));
        nodes.forEach(n -> Assertions.assertTrue(network.contains(n)));

        // Ensure all edges are correct
        edges.forEach(e -> Assertions.assertTrue(network.neighbors(e.getFirst()).contains(e.getSecond())));
        edges.forEach(e -> Assertions.assertTrue(network.neighbors(e.getSecond()).contains(e.getFirst())));
    }

    @Test
    public void testNetworkEdgeIndexing() {
        // This test is to ensure the node and edge indexing used for serialization
        // works as expected.
        var node1 = new TestNode(false);
        var node2 = new TestNode(false);
        var node3 = new TestNode(false);
        var node4 = new TestNode(false);

        var nodes = List.of(node1, node2, node3, node4);

        var edges = List.of(Pair.of(node1, node2), Pair.of(node2, node3), Pair.of(node3, node4));

        var network = new BasicNetwork<>(nodes, edges);

        var nodeList = network.createNodeList();
        var edgeIndices = network.createEdgeIndices();

        Assertions.assertTrue(network.edges().allMatch(e -> {
            boolean found = false;
            int indexA = nodeList.indexOf(e.getFirst());
            int indexB = nodeList.indexOf(e.getSecond());

            // See if we can find this pair
            for (var pair : edgeIndices.edges()) {
                if ((pair.getFirst() == indexA && pair.getSecond() == indexB)
                        || pair.getFirst() == indexB && pair.getSecond() == indexA) {
                    found = true;
                    break;
                }
            }

            return found;
        }), "Node index list does not match real edges.");
    }

    @Test
    public void testNetworkSingleNodeWithEmptyEdges() {
        // This test is to ensure the node and edge indexing used for serialization
        // works as expected.
        var node1 = new TestNode(false);

        Assertions.assertDoesNotThrow(() -> new BasicNetwork<>(List.of(node1), List.of()));
        Assertions.assertTrue(node1.isValid());
    }

    @Test
    public void testNetworkSingleNodeWithEdges() {
        // This test is to ensure the node and edge indexing used for serialization
        // works as expected.
        var node1 = new TestNode(false);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BasicNetwork<>(List.of(node1), List.of(Pair.of(node1, node1))));
        Assertions.assertFalse(node1.isValid());
    }

    @Test
    public void testCreateNetworkNoNodesNoEdges() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BasicNetwork<TestNode>(List.of(), List.of()));
    }

    @Test
    public void testNetworkCreateWithInvalidEdgeIndices() {
        // This test is to ensure the node and edge indexing used for serialization
        // works as expected.
        var node1 = new TestNode(false);

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> new BasicNetwork<>(List.of(node1), new Network.IndexedEdgeList(List.of(Pair.of(1, 5)))));
        Assertions.assertFalse(node1.isValid());
    }

}
