package com.enderio.enderio.api.conduits.connection.config;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Experimental
public record ConnectionConfigType<T extends ConnectionConfig>(MapCodec<T> codec,
                                                               StreamCodec<FriendlyByteBuf, T> streamCodec, Supplier<T> defaultSupplier) {
    public T getDefault() {
        return defaultSupplier.get();
    }
}
