package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// TODO: Not a big fan of this..
public record EmitParticlesPacket(List<EmitParticlePacket> particles) implements CustomPacketPayload {

    public static final Type<EmitParticlesPacket> TYPE = new Type<>(EnderCore.loc("emit_particles"));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EmitParticlesPacket> STREAM_CODEC =
        EmitParticlePacket.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(EmitParticlesPacket::new, EmitParticlesPacket::particles);
    // @formatter:on

    public EmitParticlesPacket() {
        this(new ArrayList<>());
    }

    @Override
    public Type<EmitParticlesPacket> type() {
        return TYPE;
    }

    public void add(EmitParticlePacket particlePacket) {
        particles.add(particlePacket);
    }

    public void add(ParticleOptions type, double x, double y, double z) {
        add(type, x, y, z, 0, 0, 0);
    }

    public void add(ParticleOptions type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        add(new EmitParticlePacket(type, x, y, z, xSpeed, ySpeed, zSpeed));
    }

    public void add(BlockPos pos, ParticleOptions type) {
        add(new EmitParticlePacket(type, pos));
    }
}
