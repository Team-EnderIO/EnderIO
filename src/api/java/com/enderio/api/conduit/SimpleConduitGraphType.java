package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface SimpleConduitGraphType<T extends ConduitData<T>> extends ConduitGraphType<Void, ConduitGraphContext.Dummy, T> {
    @Override
    @Nullable
    default ConduitGraphContext.Dummy createGraphContext(Void unused) {
        return null;
    }
}
