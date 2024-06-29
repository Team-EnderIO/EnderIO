package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNode;
import dev.gigaherz.graph3.Graph;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Wrap the graph for public API consumption.
 */
public record WrappedConduitNetwork<TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>>(Graph<InternalGraphContext<TContext>> graph)
    implements ConduitNetwork<TContext, TData> {

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
