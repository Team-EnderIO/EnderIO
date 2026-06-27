package com.enderio.enderio.tests.conduits.network.query;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.NodeUpdated;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.query.BlockConnectionsQuery;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkQueryUpdateContext;
import com.enderio.enderio.api.conduits.network.query.ConduitNetworkRebuildContext;
import com.enderio.enderio.api.conduits.network.query.TickingNodesQuery;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class BlockConnectionsQueryTests {
    @Test
    public void fullRebuild() {
        // Arrange.
        var connectionsQuery = BlockConnectionsQuery.TYPE.create();

        var node = mock(ConduitNode.class);

        var expectedConnection = new ConduitBlockConnection(node, Direction.UP);
        when(node.isConnectedToBlock(expectedConnection.connectionSide())).thenReturn(true);
        when(node.isConnectedToBlock(argThat(d -> d != expectedConnection.connectionSide()))).thenReturn(false);

        var tickingQuery = mock(TickingNodesQuery.class);
        when(tickingQuery.tickingNodes()).thenReturn(Set.of(node));

        var context = mock(ConduitNetworkRebuildContext.class);
        when(context.getDependency(TickingNodesQuery.TYPE)).thenReturn(tickingQuery);

        // Act.
        connectionsQuery.fullRebuild(context);

        // Assert.
        verify(context, never()).network();
        verify(context, atMostOnce()).getDependency(TickingNodesQuery.TYPE);
        verify(tickingQuery, atMostOnce()).tickingNodes();

        Assertions.assertTrue(connectionsQuery.allConnections().contains(expectedConnection));
        Assertions.assertTrue(connectionsQuery.connectionsFor(node).contains(expectedConnection));
    }

    @Test
    public void processUpdates_AddedTickingNode_AddsNewConnections() {
        // Arrange.
        var query = BlockConnectionsQuery.TYPE.create();

        var node = mock(ConduitNode.class);

        var expectedConnection = new ConduitBlockConnection(node, Direction.UP);
        when(node.isConnectedToBlock(expectedConnection.connectionSide())).thenReturn(true);
        when(node.isConnectedToBlock(argThat(d -> d != expectedConnection.connectionSide()))).thenReturn(false);

        var context = mock(ConduitNetworkQueryUpdateContext.class);
        when(context.getDependencyChanges(TickingNodesQuery.TYPE)).thenReturn(new TickingNodesQuery.UpdateResult(Set.of(node), Set.of()));

        // Act.
        var result = query.processUpdates(context);

        // Assert.
        verify(context, never()).network();
        Assertions.assertTrue(query.allConnections().contains(expectedConnection));
        Assertions.assertTrue(query.connectionsFor(node).contains(expectedConnection));
        Assertions.assertTrue(result.didChange());
        Assertions.assertTrue(result.addedConnections().contains(expectedConnection));
        Assertions.assertEquals(1, result.addedConnections().size());
        Assertions.assertTrue(result.removedConnections().isEmpty());
    }

    @Nested
    public class Updates {
        private BlockConnectionsQuery query;
        private ConduitNode node;
        private ConduitBlockConnection defaultConnection;
        
        @BeforeEach
        public void setup() {
            query = BlockConnectionsQuery.TYPE.create();

            node = mock(ConduitNode.class);

            defaultConnection = new ConduitBlockConnection(node, Direction.UP);
            when(node.isConnectedToBlock(defaultConnection.connectionSide())).thenReturn(true);
            when(node.isConnectedToBlock(argThat(d -> d != defaultConnection.connectionSide()))).thenReturn(false);

            var tickingQuery = mock(TickingNodesQuery.class);
            when(tickingQuery.tickingNodes()).thenReturn(Set.of(node));

            var context = mock(ConduitNetworkRebuildContext.class);
            when(context.getDependency(TickingNodesQuery.TYPE)).thenReturn(tickingQuery);
            query.fullRebuild(context);
        }

        @Test
        public void processUpdates_NodeUpdated_NewConnection() {
            // Arrange.
            var newExpectedConnection = new ConduitBlockConnection(node, Direction.DOWN);
            when(node.isConnectedToBlock(newExpectedConnection.connectionSide())).thenReturn(true);

            var context = mock(ConduitNetworkQueryUpdateContext.class);
            when(context.changes()).thenReturn(List.of(new NodeUpdated(node)));
            when(context.getDependencyChanges(TickingNodesQuery.TYPE)).thenReturn(TickingNodesQuery.UpdateResult.EMPTY);

            // Act.
            var result = query.processUpdates(context);

            // Assert.
            verify(context, never()).network();
            Assertions.assertTrue(query.allConnections().contains(defaultConnection));
            Assertions.assertTrue(query.allConnections().contains(newExpectedConnection));
            Assertions.assertTrue(query.connectionsFor(node).contains(defaultConnection));
            Assertions.assertTrue(query.connectionsFor(node).contains(newExpectedConnection));
            Assertions.assertTrue(result.didChange());
            Assertions.assertTrue(result.addedConnections().contains(newExpectedConnection));
            Assertions.assertEquals(1, result.addedConnections().size());
            Assertions.assertTrue(result.removedConnections().isEmpty());
        }

        @Test
        public void processUpdates_NodeUpdated_NoChanges() {
            // Arrange.
            var context = mock(ConduitNetworkQueryUpdateContext.class);
            when(context.changes()).thenReturn(List.of(new NodeUpdated(node)));
            when(context.getDependencyChanges(TickingNodesQuery.TYPE)).thenReturn(TickingNodesQuery.UpdateResult.EMPTY);

            // Act.
            var result = query.processUpdates(context);

            // Assert.
            verify(context, never()).network();
            Assertions.assertTrue(query.allConnections().contains(defaultConnection));
            Assertions.assertTrue(query.connectionsFor(node).contains(defaultConnection));
            Assertions.assertFalse(result.didChange());
            Assertions.assertTrue(result.addedConnections().isEmpty());
            Assertions.assertTrue(result.removedConnections().isEmpty());
        }
    }
}
