package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.souldata.FarmSoul;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FarmStationSoulPacket(Map<ResourceLocation, FarmSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<FarmStationSoulPacket> TYPE = new Type<>(EnderIO.loc("farm_soul"));

    public static final StreamCodec<ByteBuf, FarmStationSoulPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, FarmSoul.STREAM_CODEC),
            FarmStationSoulPacket::map, FarmStationSoulPacket::new);

    public FarmStationSoulPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readResourceLocation,
                buff -> new FarmSoul.SoulData(buff.readResourceLocation(), buff.readFloat(), buff.readInt(),
                        buff.readFloat())));
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
