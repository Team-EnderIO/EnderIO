package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record EmitParticlePacket(ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("emit_particle");

    public EmitParticlePacket(ParticleOptions type, BlockPos pos) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }

    public EmitParticlePacket(ParticleOptions type, BlockPos pos, double xSpeed, double ySpeed, double zSpeed) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xSpeed, ySpeed, zSpeed);
    }

    public EmitParticlePacket(FriendlyByteBuf buf) {
        this(
            readParticle(buf, Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.get(buf.readResourceLocation()))),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble()
        );
    }

    private static <T extends ParticleOptions> T readParticle(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(particleOptions.getType())));
        writeInto.writeDouble(x);
        writeInto.writeDouble(y);
        writeInto.writeDouble(z);
        writeInto.writeDouble(xSpeed);
        writeInto.writeDouble(ySpeed);
        writeInto.writeDouble(zSpeed);
        particleOptions.writeToNetwork(writeInto);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
