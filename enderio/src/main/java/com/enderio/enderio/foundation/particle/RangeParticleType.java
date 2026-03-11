package com.enderio.enderio.foundation.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class RangeParticleType extends ParticleType<RangeParticleData> {

    public RangeParticleType() {
        super(false);
    }

    @NotNull
    @Override
    public MapCodec<RangeParticleData> codec() {
        return RangeParticleData.CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, RangeParticleData> streamCodec() {
        return RangeParticleData.STREAM_CODEC;
    }
}
