package com.enderio.api.conduit;

import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;

public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    T mergeWith(T other);

    T copy();

    ConduitNetworkContextType<T> type();
}
