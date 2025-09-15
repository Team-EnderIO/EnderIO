package com.enderio.base.common.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SoulParticleType extends ParticleType<SoulParticleData> {

    public SoulParticleType() {
        super(false);
    }

    @NotNull
    @Override
    public MapCodec<SoulParticleData> codec() {
        return SoulParticleData.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SoulParticleData> streamCodec() {
        return SoulParticleData.STREAM_CODEC;
    }
}
