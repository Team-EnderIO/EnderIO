package com.enderio.machines.common.network;

import com.enderio.core.common.network.Packet;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.Optional;

public class SoulEngineSoulPacket implements Packet {

    private final Map<ResourceLocation, EngineSoul.SoulData> map;

    public SoulEngineSoulPacket(Map<ResourceLocation, EngineSoul.SoulData> map) {
        this.map = map;
    }

    public SoulEngineSoulPacket(FriendlyByteBuf buf) {
        this.map = buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
            new EngineSoul.SoulData(buff.readResourceLocation(), buff.readUtf(), buff.readInt(), buff.readInt())
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
        context.enqueueWork(() -> EngineSoul.ENGINE.map = this.map);
        context.setPacketHandled(true);
    }

    public static class Handler extends PacketHandler<SoulEngineSoulPacket> {

        @Override
        public SoulEngineSoulPacket fromNetwork(FriendlyByteBuf buf) {
            return new SoulEngineSoulPacket(buf);
        }

        @Override
        public void toNetwork(SoulEngineSoulPacket packet, FriendlyByteBuf buf) {
            packet.write(buf);
        }

        @Override
        public Optional<NetworkDirection> getDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
