package com.enderio.conduits.api.network.node;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public record NodeDataType<T extends NodeData>(@Nullable MapCodec<T> codec, Supplier<T> factory) {
    public boolean isPersistent() {
        return codec != null;
    }

    public MapCodec<T> codecOrThrow() {
        Preconditions.checkState(codec != null, this + " is not persistent node data");
        return codec;
    }
}
