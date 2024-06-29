package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitGraphContext;
import com.enderio.api.conduit.ConduitNode;
import dev.gigaherz.graph3.Graph;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Wrap the graph for public API consumption.
 */
public record WrappedConduitGraph<TContext extends ConduitGraphContext<TContext>, TData extends ConduitData<TData>>(Graph<InternalGraphContext<TContext>> graph)
    implements ConduitGraph<TContext, TData> {

    @Override
    public Collection<ConduitNode<TContext, TData>> getNodes() {
        //noinspection unchecked
        return graph.getObjects().stream()
            .map(object -> (ConduitNode<TContext, TData>) object)
            .toList();
    }

    @Nullable
    @Override
    public TContext getContext() {
        InternalGraphContext<TContext> wrappedContext = graph.getContextData();
        if (wrappedContext != null) {
            return wrappedContext.context();
        }

        return null;
    }
}
