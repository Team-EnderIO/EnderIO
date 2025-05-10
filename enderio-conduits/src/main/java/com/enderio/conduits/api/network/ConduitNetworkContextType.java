package com.enderio.conduits.api.network;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * A type of conduit network context.
 * @param codec The codec used to serialize and deserialize the context. Can be null for non-persistent contexts.
 * @param factory A factory for creating new instances of the context.
 */
public record ConduitNetworkContextType<T extends ConduitNetworkContext<T>>(@Nullable MapCodec<T> codec,
        Supplier<T> factory) {
    public boolean isPersistent() {
        return codec != null;
    }

    public MapCodec<T> codecOrThrow() {
        Preconditions.checkState(codec != null, this + " is not a persistent context");
        return codec;
    }
}
