package com.enderio.machines.common.network;

import com.enderio.EnderIO;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record FarmStationSoulPacket(Map<ResourceLocation, FarmSoul.SoulData> map) implements CustomPacketPayload {

    public static final ResourceLocation ID = EnderIO.loc("farming_station_soul");

    public FarmStationSoulPacket(FriendlyByteBuf buf) {
        this(
            buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
                new FarmSoul.SoulData(buff.readResourceLocation(), buff.readFloat(), buff.readInt(), buff.readFloat())
            )
        );
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeMap(map, FriendlyByteBuf::writeResourceLocation, (buf, soulData) -> {
            buf.writeResourceLocation(soulData.entitytype());
            buf.writeFloat(soulData.bonemeal());
            buf.writeInt(soulData.seeds());
            buf.writeFloat(soulData.power());
        });
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
