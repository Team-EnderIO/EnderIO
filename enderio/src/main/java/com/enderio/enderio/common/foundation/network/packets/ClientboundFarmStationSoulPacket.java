package com.enderio.enderio.common.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.foundation.souldata.FarmSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record ClientboundFarmStationSoulPacket(Map<ResourceLocation, FarmSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<ClientboundFarmStationSoulPacket> TYPE = new Type<>(EnderIO.rl("farm_soul"));

    public static final StreamCodec<ByteBuf, ClientboundFarmStationSoulPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, FarmSoul.STREAM_CODEC),
            ClientboundFarmStationSoulPacket::map, ClientboundFarmStationSoulPacket::new);

    public ClientboundFarmStationSoulPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation,
                buff -> new FarmSoul.SoulData(buff.readResourceLocation(), buff.readFloat(), buff.readInt(),
                        buff.readFloat())));
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
