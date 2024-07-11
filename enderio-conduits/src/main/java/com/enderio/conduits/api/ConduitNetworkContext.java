package com.enderio.conduits.api;

public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    T mergeWith(T other);

    T copy();

    ConduitNetworkContextType<T> type();
}
