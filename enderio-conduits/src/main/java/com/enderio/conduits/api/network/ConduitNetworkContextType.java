package com.enderio.conduits.api.network;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public record ConduitNetworkContextType<T extends ConduitNetworkContext<T>>(@Nullable MapCodec<T> codec,
        Supplier<T> factory) {
}
