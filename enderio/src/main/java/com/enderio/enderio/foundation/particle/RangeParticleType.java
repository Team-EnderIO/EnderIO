package com.enderio.enderio.foundation.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class RangeParticleType extends ParticleType<RangeParticleData> {

    public RangeParticleType() {
        super(false);
    }

    @NonNull
    @Override
    public MapCodec<RangeParticleData> codec() {
        return RangeParticleData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, RangeParticleData> streamCodec() {
        return RangeParticleData.STREAM_CODEC;
    }
}
