package com.enderio.conduits.api.network;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public record ConduitNetworkContextType<T extends ConduitNetworkContext<T>>(@Nullable MapCodec<T> codec, Supplier<T> factory) {
    public boolean isPersistent() {
        return codec != null;
    }

    public MapCodec<T> codecOrThrow() {
        Preconditions.checkState(codec != null, this + " is not a persistent context");
        return codec;
    }
}
