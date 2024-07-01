package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface SimpleConduit<TType extends SimpleConduit<TType, TData>, TData extends ConduitData<TData>> extends
    Conduit<TType, ConduitNetworkContext.Dummy, TData> {
    @Override
    @Nullable
    default ConduitNetworkContext.Dummy createNetworkContext(ConduitNetwork<ConduitNetworkContext.Dummy, TData> network) {
        return null;
    }
}
