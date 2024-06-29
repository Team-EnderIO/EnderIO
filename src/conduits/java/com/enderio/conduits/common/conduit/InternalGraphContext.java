package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import dev.gigaherz.graph3.ContextDataFactory;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;

public record InternalGraphContext<T extends ConduitNetworkContext<T>>(T context) implements Mergeable<InternalGraphContext<T>> {
    @Override
    public InternalGraphContext<T> mergeWith(InternalGraphContext<T> other) {
        return new InternalGraphContext<>(context.mergeWith(other.context));
    }

    @Override
    public InternalGraphContext<T> splitFor(Graph<InternalGraphContext<T>> graph, Graph<InternalGraphContext<T>> graph1) {
        return new InternalGraphContext<>(context.splitFor(new WrappedConduitNetwork<>(graph), new WrappedConduitNetwork<>(graph1)));
    }

    public static <T extends ConduitNetworkContext<T>> InternalGraphContext<T> of(T context) {
        return new InternalGraphContext<>(context);
    }

    public static <T extends ConduitNetworkContext<T>> ContextDataFactory<InternalGraphContext<T>> factoryFor(ConduitType<?, T, ?> conduitType) {
        return graph -> {
            var context = conduitType.createGraphContext(new WrappedConduitNetwork<>(graph));
            return context == null ? null : new InternalGraphContext<>(context);
        };
    }
}
