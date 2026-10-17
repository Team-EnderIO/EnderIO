package com.enderio.enderio.foundation.soul.ingredient;

import com.enderio.enderio.api.soul.ingredient.SoulIngredientType;
import com.enderio.enderio.init.EIODataMaps;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SolarSoulIngredient extends BaseSoulDataIngredient {

    public static final SolarSoulIngredient INSTANCE = new SolarSoulIngredient();

    private static final MapCodec<SolarSoulIngredient> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, SolarSoulIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final SoulIngredientType<SolarSoulIngredient> TYPE = new SoulIngredientType<>(CODEC, STREAM_CODEC);

    private SolarSoulIngredient() {
        super(EIODataMaps.SOLAR_SOUL);
    }
    @Override
    public SoulIngredientType<?> getType() {
        return TYPE;
    }
}
