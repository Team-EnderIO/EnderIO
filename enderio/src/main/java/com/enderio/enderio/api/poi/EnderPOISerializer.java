package com.enderio.enderio.api.poi;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface EnderPOISerializer <T extends EnderPOI> {
    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
