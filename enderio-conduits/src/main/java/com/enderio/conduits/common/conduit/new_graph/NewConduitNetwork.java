package com.enderio.conduits.common.conduit.new_graph;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.core.common.graph.Network;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class NewConduitNetwork<T extends ConnectionConfig> extends Network<NewConduitNetwork<T>, NewConduitNode<T>> {

    private final Holder<Conduit<?, T>> conduit;

    @Nullable
    private ConduitNetworkContext<?> context;

    protected NewConduitNetwork(Holder<Conduit<?, T>> conduit, NewConduitNode<T> initialNode) {
        super(initialNode);
        this.conduit = conduit;
    }

    protected NewConduitNetwork(Holder<Conduit<?, T>> conduit) {
        this.conduit = conduit;
    }

    public ConnectionConfigType<T> connectionConfigType() {
        return conduit.value().connectionConfigType();
    }

    // TODO: Queries and caching :)

    // region Context

    public boolean hasContext(ConduitNetworkContextType<?> type) {
        return context != null && context.type() == type;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> @Nullable C getContext(ConduitNetworkContextType<C> type) {
        if (context != null && context.type() == type) {
            return (C) context;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> C getOrCreateContext(ConduitNetworkContextType<C> type) {
        if (context != null && context.type() == type) {
            return (C) context;
        }

        context = type.factory().get();
        return (C) context;
    }

    // endregion

    // region Network Impl

    @Override
    protected NewConduitNetwork<T> createEmpty() {
        return new NewConduitNetwork<>(conduit);
    }

    @Override
    protected void onNetworkChanged() {
        // TODO: This is where we'll recompute our caches.
    }

    @Override
    protected void onMerged(NewConduitNetwork<T> other) {
        if (context != null && other.context != null) {
            context = context.mergeWith(other.castContext());
        } else if (context == null && other.context != null) {
            context = other.context;
        }
    }

    private <Z extends ConduitNetworkContext<Z>> Z castContext() {
        // noinspection unchecked
        return (Z) context;
    }

    @Override
    protected void onGraphSplit(Set<NewConduitNetwork<T>> newNetworks) {
        if (context == null) {
            return;
        }

        // TODO: Implement proper split method for contexts!

        // Handle the new graphs first
        for (var newNetwork : newNetworks) {
            newNetwork.context = context.copy();
        }

        context = context.copy();
    }

    // endregion
}
