package com.enderio.enderio.tests.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.GraphRebuilt;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.query.TickingNodesQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class TickingNodesQueryTests {
    @Test
    public void graphRebuildQueriesEntireNetwork() {
        // Arrange.
        var query = TickingNodesQuery.TYPE.factory().get();
        var network = mock(ConduitNetwork.class);

        ConduitNode exampleNode = mock(ConduitNode.class);
        when(exampleNode.isTicking()).thenReturn(true);

        when(network.nodes()).thenAnswer(ignored -> Set.of(exampleNode));

        // Act.
        boolean didUpdate = query.processUpdates(network, Set.of(GraphRebuilt.INSTANCE));

        // Assert.
        Assertions.assertTrue(didUpdate);
        verify(network).nodes(); // did we scan the entire network?
        Assertions.assertTrue(query.query().contains(exampleNode));
    }
}
