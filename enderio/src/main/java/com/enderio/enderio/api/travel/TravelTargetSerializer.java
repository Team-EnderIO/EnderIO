package com.enderio.enderio.api.travel;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface TravelTargetSerializer<T extends TravelTarget> {
    MapCodec<T> codec();
    StreamCodec<FriendlyByteBuf, T> streamCodec();
}
