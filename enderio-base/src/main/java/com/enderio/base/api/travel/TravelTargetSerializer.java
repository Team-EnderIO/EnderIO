package com.enderio.base.api.travel;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface TravelTargetSerializer<T extends TravelTarget> {
    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
