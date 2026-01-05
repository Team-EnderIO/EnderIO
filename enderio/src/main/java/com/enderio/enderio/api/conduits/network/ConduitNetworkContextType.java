package com.enderio.enderio.api.conduits.network;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A type of conduit network context.
 * @param codec The codec used to serialize and deserialize the context. Can be null for non-persistent contexts.
 * @param factory A factory for creating new instances of the context.
 */
@ApiStatus.Experimental
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
