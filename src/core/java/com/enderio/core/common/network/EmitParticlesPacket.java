package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmitParticlesPacket implements Packet {

    private final List<EmitParticlePacket> particles = new ArrayList<>();

    public EmitParticlesPacket() {
    }

    public EmitParticlesPacket(FriendlyByteBuf buf) {
        int numParticles = buf.readInt();
        for (int i = 0; i < numParticles; i++) {
            particles.add(new EmitParticlePacket(buf));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(particles.size());
        for (EmitParticlePacket particle : particles) {
            particle.write(buf);
        }
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

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return true;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        for (EmitParticlePacket particle : particles) {
            particle.handle(context);
        }
    }

    public static class Handler extends PacketHandler<EmitParticlesPacket> {

        @Override
        public EmitParticlesPacket fromNetwork(FriendlyByteBuf buf) {
            return new EmitParticlesPacket(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }

        @Override
        public void toNetwork(EmitParticlesPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }
    }

}
