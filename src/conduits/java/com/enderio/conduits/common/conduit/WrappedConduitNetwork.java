package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkContextType;
import com.enderio.api.conduit.ConduitNode;
import dev.gigaherz.graph3.Graph;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Wrap the graph for public API consumption.
 */
public record WrappedConduitNetwork(Graph<ConduitGraphContext> graph) implements ConduitNetwork {

    @Override
    public Collection<ConduitNode> getNodes() {
        //noinspection unchecked
        return graph.getObjects().stream()
            .map(object -> (ConduitNode) object)
            .toList();
    }

    @Override
    public boolean hasContext(ConduitNetworkContextType<?> type) {
        return graph.getContextData().hasContext(type);
    }

    @Override
    @Nullable
    public <T extends ConduitNetworkContext<T>> T getContext(ConduitNetworkContextType<T> type) {
        return graph.getContextData().getContext(type);
    }

    @Override
    public <T extends ConduitNetworkContext<T>> T getOrCreateContext(ConduitNetworkContextType<T> type) {
        return graph.getContextData().getOrCreateContext(type);
    }
}
