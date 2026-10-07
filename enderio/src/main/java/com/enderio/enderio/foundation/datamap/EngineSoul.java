package com.enderio.enderio.foundation.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public record EngineSoul(FluidIngredient fluidIngredient, int powerPerMb, int tickPerMb) {
    public static final Codec<EngineSoul> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(
                FluidIngredient.CODEC.fieldOf("fluid").forGetter(EngineSoul::fluidIngredient),
                Codec.INT.fieldOf("powerPerMb").forGetter(EngineSoul::powerPerMb),
                Codec.INT.fieldOf("tickPerMb").forGetter(EngineSoul::tickPerMb))
            .apply(soulDataInstance, EngineSoul::new));
}
