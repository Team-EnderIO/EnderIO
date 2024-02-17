package com.enderio.machines.common.network;

import com.enderio.EnderIO;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SoulEngineSoulPacket(Map<ResourceLocation, EngineSoul.SoulData> map)
    implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderIO.loc("soul_engine_soul");

    public SoulEngineSoulPacket(FriendlyByteBuf buf) {
        this(
            buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
                new EngineSoul.SoulData(buff.readResourceLocation(), buff.readUtf(), buff.readInt(), buff.readInt())
            )
        );
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeMap(map, FriendlyByteBuf::writeResourceLocation, (buf, soulData) -> {
            buf.writeResourceLocation(soulData.entitytype());
            buf.writeUtf(soulData.fluid());
            buf.writeInt(soulData.powerpermb());
            buf.writeInt(soulData.tickpermb());
        });
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> EngineSoul.ENGINE.map = this.map);
        context.setPacketHandled(true);
    }

}
