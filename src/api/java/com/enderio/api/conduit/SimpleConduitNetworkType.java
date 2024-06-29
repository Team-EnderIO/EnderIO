package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

public interface SimpleConduitNetworkType<T extends ConduitData<T>> extends ConduitNetworkType<Void, ConduitNetworkContext.Dummy, T> {
    @Override
    @Nullable
    default ConduitNetworkContext.Dummy createNetworkContext(ConduitType<Void, ConduitNetworkContext.Dummy, T> type,
        ConduitNetwork<ConduitNetworkContext.Dummy, T> network) {
        return null;
    }

    @Override
    default int compare(Void o1, Void o2) {
        return 0;
    }
}
