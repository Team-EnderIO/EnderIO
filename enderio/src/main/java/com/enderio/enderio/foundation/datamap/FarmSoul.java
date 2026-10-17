package com.enderio.enderio.foundation.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FarmSoul(float boneMeal, int seeds, float power) {
    public static final Codec<FarmSoul> CODEC = RecordCodecBuilder.create(FarmSoulInstance -> FarmSoulInstance
        .group(
            Codec.FLOAT.optionalFieldOf("bonemeal", 1f).forGetter(FarmSoul::boneMeal),
            Codec.INT.optionalFieldOf("seeds", 0).forGetter(FarmSoul::seeds),
            Codec.FLOAT.optionalFieldOf("power", 1f).forGetter(FarmSoul::power))
        .apply(FarmSoulInstance, FarmSoul::new));
}
