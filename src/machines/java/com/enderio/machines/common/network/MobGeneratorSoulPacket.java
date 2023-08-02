package com.enderio.machines.common.network;

import com.enderio.core.common.network.Packet;
import com.enderio.machines.common.souldata.GeneratorSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobGeneratorSoulPacket implements Packet {

    public static Map<ResourceLocation, GeneratorSoul.SoulData> SYNCED_DATA = new HashMap<>();

    private final Map<ResourceLocation, GeneratorSoul.SoulData> map;

    public MobGeneratorSoulPacket(Map<ResourceLocation, GeneratorSoul.SoulData> map) {
        this.map = map;
    }

    public MobGeneratorSoulPacket(FriendlyByteBuf buf) {
        this.map = buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
            new GeneratorSoul.SoulData(buff.readResourceLocation(), buff.readUtf(), buff.readInt(), buff.readInt())
        );
    }

    protected void write(FriendlyByteBuf writeInto) {
        writeInto.writeMap(map, FriendlyByteBuf::writeResourceLocation, (buf, soulData) -> {
            buf.writeResourceLocation(soulData.entitytype());
            buf.writeUtf(soulData.fluid());
            buf.writeInt(soulData.powerpermb());
            buf.writeInt(soulData.tickpermb());
        });
    }
    @Override
    public boolean isValid(NetworkEvent.Context context) {
        return context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> SYNCED_DATA = this.map);
        context.setPacketHandled(true);
    }

    public static class Handler extends PacketHandler<MobGeneratorSoulPacket> {

        @Override
        public MobGeneratorSoulPacket fromNetwork(FriendlyByteBuf buf) {
            return new MobGeneratorSoulPacket(buf);
        }

        @Override
        public void toNetwork(MobGeneratorSoulPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
