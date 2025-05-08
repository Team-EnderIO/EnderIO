package com.enderio.core;

import com.enderio.core.common.graph.BasicNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.datafixers.util.Pair;
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

        // Connect their secondary nodes (this will discard node2's and node4's networks)
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

        // Remove node2 from the network (should cause a split for node1 and node3, and should invalidate node2)
        Assertions.assertDoesNotThrow(() -> node2.getNetwork().remove(node2));
        Assertions.assertFalse(node2.isValid());

        // Verify graph split
        Assertions.assertNotEquals(node1.getNetwork(), node3.getNetwork());
    }

    @Test void testNetworkConnectMany() {
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

        var edges = List.of(
            Pair.of(node1, node2),
            Pair.of(node2, node3),
            Pair.of(node3, node4));

        var anyAdditionalNetworks = new AtomicBoolean(false);
        var network = new BasicNetwork<>(nodes, edges, n -> anyAdditionalNetworks.set(true));

        // Ensure no split occurred
        Assertions.assertFalse(anyAdditionalNetworks.get(), "Should not have created any additional networks.");

        // Ensure all networks are correct.
        nodes.forEach(n -> Assertions.assertEquals(network, n.getNetwork()));
        nodes.forEach(n -> Assertions.assertTrue(network.contains(n)));

        // Ensure all edges are correct
        edges.forEach(e -> Assertions.assertTrue(network.neighbors(e.getFirst()).contains(e.getSecond())));
        edges.forEach(e -> Assertions.assertTrue(network.neighbors(e.getSecond()).contains(e.getFirst())));
    }

    @Test
    public void testNetworkConstructionWithSplitEdges() {
        var node1 = new TestNode(false);
        var node2 = new TestNode(false);
        var node3 = new TestNode(false);
        var node4 = new TestNode(false);

        var nodes = List.of(node1, node2, node3, node4);

        // These edges will yield two separate networks.
        var edges = List.of(
            Pair.of(node1, node2),
            Pair.of(node3, node4));

        var anyAdditionalNetworks = new AtomicBoolean(false);

        // Note - creating a network like this makes no guarantees about which nodes end up on which side of the split.
        var newNetwork = new BasicNetwork<>(nodes, edges, n -> anyAdditionalNetworks.set(true));

        // Ensure no split occurred
        Assertions.assertTrue(anyAdditionalNetworks.get(), "Should have created an additional networks.");

        // Ensure nodes are in the correct networks
        Assertions.assertEquals(node1.getNetwork(), node2.getNetwork());
        Assertions.assertEquals(node3.getNetwork(), node4.getNetwork());

        var network1 = node1.getNetwork();
        Assertions.assertTrue(network1.contains(node1));
        Assertions.assertTrue(network1.contains(node2));
        Assertions.assertFalse(network1.contains(node3));
        Assertions.assertFalse(network1.contains(node4));

        var network2 = node3.getNetwork();
        Assertions.assertTrue(network2.contains(node3));
        Assertions.assertTrue(network2.contains(node4));
        Assertions.assertFalse(network2.contains(node1));
        Assertions.assertFalse(network2.contains(node2));
    }

}
