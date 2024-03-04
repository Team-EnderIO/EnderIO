package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// TODO: Not a big fan of this..
public record EmitParticlesPacket(List<EmitParticlePacket> particles) implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderCore.loc("emit_particles");

    public EmitParticlesPacket() {
        this(new ArrayList<>());
    }

    public EmitParticlesPacket(FriendlyByteBuf buf) {
        this(new ArrayList<>());
        int numParticles = buf.readInt();
        for (int i = 0; i < numParticles; i++) {
            particles.add(new EmitParticlePacket(buf));
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(particles.size());
        for (EmitParticlePacket particle : particles) {
            particle.write(buf);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
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
