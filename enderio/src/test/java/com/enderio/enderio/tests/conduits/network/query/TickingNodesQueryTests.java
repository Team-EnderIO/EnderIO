package com.enderio.enderio.tests.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.NodeAdded;
import com.enderio.enderio.api.conduits.network.NodeRemoved;
import com.enderio.enderio.api.conduits.network.NodeUpdated;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.query.TickingNodesQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TickingNodesQueryTests {
    @Test
    public void fullRebuild_ScansEntireNetwork_OnlyAddsTicking() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode tickingNode = mock(ConduitNode.class);
        when(tickingNode.isTicking()).thenReturn(true);

        ConduitNode nonTickingNode = mock(ConduitNode.class);
        when(nonTickingNode.isTicking()).thenReturn(false);

        when(network.nodes()).thenAnswer(ignored -> Set.of(tickingNode, nonTickingNode));

        // Act.
        query.fullRebuild(network);

        // Assert.
        verify(network).nodes();
        Assertions.assertTrue(query.tickingNodes().contains(tickingNode));
        Assertions.assertFalse(query.tickingNodes().contains(nonTickingNode));
    }

    @Test
    public void processUpdates_AddedNode_IsTicking_Added() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(true);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeAdded(node)));

        // Assert.
        Assertions.assertTrue(result.didChange());
        Assertions.assertTrue(result.addedNodes().contains(node));
        Assertions.assertEquals(1, result.addedNodes().size());
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertTrue(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_AddedNode_IsNotTicking_NoChange() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(false);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeAdded(node)));

        // Assert.
        Assertions.assertFalse(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertFalse(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_RemovedNode_WasTicking_Removed() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.create();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(true);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeRemoved(node)));

        // Assert.
        Assertions.assertTrue(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertTrue(result.removedNodes().contains(node));
        Assertions.assertEquals(1, result.removedNodes().size());
        Assertions.assertFalse(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_RemovedNode_WasNotTicking_Removed() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(false);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeRemoved(node)));

        // Assert.
        Assertions.assertFalse(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertFalse(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_NodeUpdated_IsNowTicking_Added() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(false);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        when(node.isTicking()).thenReturn(true);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeUpdated(node)));

        // Assert.
        Assertions.assertTrue(result.didChange());
        Assertions.assertEquals(1, result.addedNodes().size());
        Assertions.assertTrue(result.addedNodes().contains(node));
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertTrue(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_NodeUpdated_IsNowNotTicking_Added() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(true);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        when(node.isTicking()).thenReturn(false);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeUpdated(node)));

        // Assert.
        Assertions.assertTrue(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertEquals(1, result.removedNodes().size());
        Assertions.assertTrue(result.removedNodes().contains(node));
        Assertions.assertFalse(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_NodeUpdated_IsStillTicking_NoChange() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(true);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeUpdated(node)));

        // Assert.
        Assertions.assertFalse(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertTrue(query.tickingNodes().contains(node));
    }

    @Test
    public void processUpdates_NodeUpdated_IsStillNotTicking_NoChange() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode node = mock(ConduitNode.class);
        when(node.isTicking()).thenReturn(false);

        when(network.nodes()).thenAnswer(ignored -> Set.of(node));
        query.fullRebuild(network);

        // Act.
        var result = query.processUpdates(network, List.of(new NodeUpdated(node)));

        // Assert.
        Assertions.assertFalse(result.didChange());
        Assertions.assertTrue(result.addedNodes().isEmpty());
        Assertions.assertTrue(result.removedNodes().isEmpty());
        Assertions.assertFalse(query.tickingNodes().contains(node));
    }

    // TODO: Decide whether NodesLoaded/NodesUnloaded makes sense, vs just pushing NodeUpdated for the entire chunk?
}
