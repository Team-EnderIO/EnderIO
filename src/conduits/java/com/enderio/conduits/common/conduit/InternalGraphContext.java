package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitGraphContext;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;

public record InternalGraphContext<T extends ConduitGraphContext<T>>(T context) implements Mergeable<InternalGraphContext<T>> {
    @Override
    public InternalGraphContext<T> mergeWith(InternalGraphContext<T> other) {
        return new InternalGraphContext<>(context.mergeWith(other.context));
    }

    @Override
    public InternalGraphContext<T> splitFor(Graph<InternalGraphContext<T>> graph, Graph<InternalGraphContext<T>> graph1) {
        return new InternalGraphContext<>(context.splitFor(new WrappedConduitGraph<>(graph), new WrappedConduitGraph<>(graph1)));
    }
}
