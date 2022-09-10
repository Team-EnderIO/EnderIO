package com.enderio.base.common.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class RangeParticleType extends ParticleType<RangeParticleData> {

    public RangeParticleType() {
        super(false, RangeParticleData.DESERIALIZER);
    }

    @NotNull
    @Override
    public Codec<RangeParticleData> codec() {
        return RangeParticleData.CODEC;
    }
}
