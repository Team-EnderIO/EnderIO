package com.enderio.base.common.network.packet;

import com.enderio.core.common.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Optional;

public class EmitParticlePacket implements Packet {

    private final double x;
    private final double y;
    private final double z;

    private final ParticleOptions particleOptions;

    public EmitParticlePacket(double x, double y,double z, ParticleOptions type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleOptions = type;
    }

    public EmitParticlePacket(BlockPos pos, ParticleOptions type) {
        this(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, type);
    }

    public EmitParticlePacket(FriendlyByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        particleOptions = readParticle(buf, Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getValue(buf.readResourceLocation())));
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }

    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeDouble(x);
        writeInto.writeDouble(y);
        writeInto.writeDouble(z);

        writeInto.writeResourceLocation(Objects.requireNonNull(particleOptions.getType().getRegistryName()));
        particleOptions.writeToNetwork(writeInto);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Objects.requireNonNull(Minecraft.getInstance().level).addParticle(particleOptions, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static class Handler extends Packet.PacketHandler<EmitParticlePacket> {

        @Override
        public EmitParticlePacket of(FriendlyByteBuf buf) {
            return new EmitParticlePacket(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }

        @Override
        public void to(EmitParticlePacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }
    }

}
