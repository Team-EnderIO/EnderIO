package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

// TODO: Not a big fan of this..
public record EmitParticlesPacket(List<EmitParticlePacket> particles) {

    public EmitParticlesPacket() {
        this(new ArrayList<>());
    }

    public EmitParticlesPacket(FriendlyByteBuf buf) {
        this((List<EmitParticlePacket>)buf.readCollection(ArrayList::new, EmitParticlePacket::new));
    }

    public static void encode(EmitParticlesPacket packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.particles, (b, i) -> i.encode(b));
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
