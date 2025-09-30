package com.enderio.enderio.api.conduits.network.node.legacy;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

@Deprecated(since = "8.0.0")
public record ConduitDataType<T extends ConduitData<T>>(MapCodec<T> codec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> factory) {
}
