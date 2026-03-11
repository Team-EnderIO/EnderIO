package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record EmitParticlePacket(ParticleOptions particleOptions, double x, double y, double z, double xSpeed,
        double ySpeed, double zSpeed) {

    public EmitParticlePacket(ParticleOptions type, BlockPos pos) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }

    public EmitParticlePacket(ParticleOptions type, BlockPos pos, double xSpeed, double ySpeed, double zSpeed) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xSpeed, ySpeed, zSpeed);
    }

    public EmitParticlePacket(FriendlyByteBuf buf) {
        this(readParticle(buf, buf.readById(BuiltInRegistries.PARTICLE_TYPE)), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.PARTICLE_TYPE, particleOptions.getType());
        particleOptions.writeToNetwork(buf);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(xSpeed);
        buf.writeDouble(ySpeed);
        buf.writeDouble(zSpeed);
    }

    private static <T extends ParticleOptions> T readParticle(FriendlyByteBuf buffer, ParticleType<T> particleType) {
        return particleType.getDeserializer().fromNetwork(particleType, buffer);
    }
}
