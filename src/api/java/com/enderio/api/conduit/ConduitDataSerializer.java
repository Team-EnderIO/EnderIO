package com.enderio.api.conduit;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ConduitDataSerializer<T extends ExtendedConduitData<T>> {
    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
