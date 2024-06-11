package com.enderio.base.common.particle;

import com.enderio.base.common.init.EIOParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RangeParticleData(int range, String color) implements ParticleOptions {

    public static final MapCodec<RangeParticleData> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("range").forGetter(RangeParticleData::range),
            Codec.STRING.fieldOf("color").forGetter(RangeParticleData::color)
        )
        .apply(instance, RangeParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RangeParticleData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        RangeParticleData::range,
        ByteBufCodecs.STRING_UTF8,
        RangeParticleData::color,
        RangeParticleData::new
    );

    @Override
    public ParticleType<?> getType() {
        return EIOParticles.RANGE_PARTICLE.get();
    }
}
