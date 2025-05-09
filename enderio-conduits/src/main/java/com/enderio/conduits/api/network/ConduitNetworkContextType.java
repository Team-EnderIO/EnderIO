package com.enderio.conduits.api.network;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public record ConduitNetworkContextType<T extends ConduitNetworkContext<T>>(@Nullable MapCodec<T> codec,
        Supplier<T> factory) {
}
