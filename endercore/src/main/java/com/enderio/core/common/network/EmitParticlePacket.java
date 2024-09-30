package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record EmitParticlePacket(ParticleOptions particleOptions, double x, double y, double z, double xSpeed,
        double ySpeed, double zSpeed) implements CustomPacketPayload {

    public static final Type<EmitParticlePacket> TYPE = new Type<>(EnderCore.loc("emit_particle"));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EmitParticlePacket> STREAM_CODEC = NeoForgeStreamCodecs.composite(
        ParticleTypes.STREAM_CODEC,
        EmitParticlePacket::particleOptions,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::x,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::y,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::z,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::xSpeed,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::ySpeed,
        ByteBufCodecs.DOUBLE,
        EmitParticlePacket::zSpeed,
        EmitParticlePacket::new
    );
    // @formatter:on

    public EmitParticlePacket(ParticleOptions type, BlockPos pos) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }

    public EmitParticlePacket(ParticleOptions type, BlockPos pos, double xSpeed, double ySpeed, double zSpeed) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
