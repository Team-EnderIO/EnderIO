package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface SimpleConduitNetworkType<TType extends SimpleConduitNetworkType<TType, TData>, TData extends ConduitData<TData>> extends ConduitType<TType, ConduitNetworkContext.Dummy, TData> {
    @Override
    @Nullable
    default ConduitNetworkContext.Dummy createNetworkContext(ConduitNetwork<ConduitNetworkContext.Dummy, TData> network) {
        return null;
    }
}
