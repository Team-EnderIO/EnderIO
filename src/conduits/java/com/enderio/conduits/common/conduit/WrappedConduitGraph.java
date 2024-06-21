package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;

import java.util.Collection;

/**
 * Wrap the graph for public API consumption.
 */
public record WrappedConduitGraph<T extends ConduitData<T>>(Graph<Mergeable.Dummy> graph)
    implements ConduitGraph<T> {

    @Override
    public Collection<ConduitNode<T>> getNodes() {
        //noinspection unchecked
        return graph.getObjects().stream()
            .map(object -> (ConduitNode<T>) object)
            .toList();
    }
}
