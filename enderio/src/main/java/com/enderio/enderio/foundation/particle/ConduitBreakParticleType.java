package com.enderio.enderio.foundation.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ConduitBreakParticleType extends ParticleType<ConduitBreakParticleOption> {

    public ConduitBreakParticleType() {
        super(false);
    }

    @Override
    public MapCodec<ConduitBreakParticleOption> codec() {
        return ConduitBreakParticleOption.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ConduitBreakParticleOption> streamCodec() {
        return ConduitBreakParticleOption.STREAM_CODEC;
    }
}
