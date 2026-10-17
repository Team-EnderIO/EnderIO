package com.enderio.enderio.api.soul.ingredient;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public record SoulIngredientType<T extends SoulIngredient>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
    public SoulIngredientType(MapCodec<T> mapCodec) {
        this(mapCodec, ByteBufCodecs.fromCodecWithRegistries(mapCodec.codec()));
    }
}
