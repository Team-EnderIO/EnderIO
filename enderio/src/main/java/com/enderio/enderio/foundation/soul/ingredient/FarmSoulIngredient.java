package com.enderio.enderio.foundation.soul.ingredient;

import com.enderio.enderio.api.soul.ingredient.SoulIngredientType;
import com.enderio.enderio.init.EIODataMaps;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class FarmSoulIngredient extends BaseSoulDataIngredient {

    public static final FarmSoulIngredient INSTANCE = new FarmSoulIngredient();

    private static final MapCodec<FarmSoulIngredient> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, FarmSoulIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final SoulIngredientType<FarmSoulIngredient> TYPE = new SoulIngredientType<>(CODEC, STREAM_CODEC);

    private FarmSoulIngredient() {
        super(EIODataMaps.FARM_SOUL);
    }
    @Override
    public SoulIngredientType<?> getType() {
        return TYPE;
    }
}
