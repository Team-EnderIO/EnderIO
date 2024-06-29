package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface SimpleConduitNetworkType<T extends ConduitData<T>> extends ConduitNetworkType<Void, ConduitNetworkContext.Dummy, T> {
    @Override
    @Nullable
    default ConduitNetworkContext.Dummy createGraphContext(Void unused) {
        return null;
    }
}
