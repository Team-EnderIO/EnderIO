package com.enderio.conduits.api.network;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.mojang.serialization.Codec;

public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    Codec<ConduitNetworkContext<?>> GENERIC_CODEC = EnderIOConduitsRegistries.CONDUIT_NETWORK_CONTEXT_TYPE.byNameCodec()
            .dispatch(ConduitNetworkContext::type, ConduitNetworkContextType::codec);

    T mergeWith(T other);

    T copy();

    ConduitNetworkContextType<T> type();
}
