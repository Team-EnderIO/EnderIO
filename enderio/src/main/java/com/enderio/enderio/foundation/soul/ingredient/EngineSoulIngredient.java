package com.enderio.enderio.foundation.soul.ingredient;

import com.enderio.enderio.api.soul.ingredient.SoulIngredientType;
import com.enderio.enderio.init.EIODataMaps;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EngineSoulIngredient extends BaseSoulDataIngredient {

    public static final EngineSoulIngredient INSTANCE = new EngineSoulIngredient();

    private static final MapCodec<EngineSoulIngredient> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, EngineSoulIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final SoulIngredientType<EngineSoulIngredient> TYPE = new SoulIngredientType<>(CODEC, STREAM_CODEC);

    private EngineSoulIngredient() {
        super(EIODataMaps.ENGINE_SOUL);
    }
    @Override
    public SoulIngredientType<?> getType() {
        return TYPE;
    }
}
