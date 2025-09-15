package com.enderio.base.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import org.jetbrains.annotations.Nullable;

public class SoulParticleProvider implements ParticleProvider<SoulParticleData> {
    @Nullable
    @Override
    public Particle createParticle(SoulParticleData data, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {

        return null;
    }
}
