package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ConduitGraph<TContext extends ConduitGraphContext<TContext>, TData extends ConduitData<TData>> {
    Collection<ConduitNode<TContext, TData>> getNodes();

    @Nullable
    TContext getContext();
}
