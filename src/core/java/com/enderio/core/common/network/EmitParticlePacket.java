package com.enderio.core.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.INetworkDirection;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.Objects;
import java.util.Optional;

public class EmitParticlePacket implements Packet {

    private final double x;
    private final double y;
    private final double z;
    private final double xSpeed;
    private final double ySpeed;
    private final double zSpeed;

    private final ParticleOptions particleOptions;

    public EmitParticlePacket(ParticleOptions type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        this.particleOptions = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
    }

    public EmitParticlePacket(ParticleOptions type, BlockPos pos) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }

    public EmitParticlePacket(ParticleOptions type, BlockPos pos, double xSpeed, double ySpeed, double zSpeed) {
        this(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xSpeed, ySpeed, zSpeed);
    }

    public EmitParticlePacket(FriendlyByteBuf buf) {
        particleOptions = readParticle(buf, Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.get(buf.readResourceLocation())));
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        xSpeed = buf.readDouble();
        ySpeed = buf.readDouble();
        zSpeed = buf.readDouble();
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == PlayNetworkDirection.PLAY_TO_CLIENT;
    }

    protected void write(FriendlyByteBuf writeInto) {
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
    public void handle(NetworkEvent.Context context) {
        Minecraft.getInstance().level.addParticle(particleOptions, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public static class Handler extends PacketHandler<EmitParticlePacket> {

        @Override
        public EmitParticlePacket fromNetwork(FriendlyByteBuf buf) {
            return new EmitParticlePacket(buf);
        }

        @Override
        public Optional<INetworkDirection<?>> getDirection() {
            return Optional.of(PlayNetworkDirection.PLAY_TO_CLIENT);
        }

        @Override
        public void toNetwork(EmitParticlePacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }
    }
}
