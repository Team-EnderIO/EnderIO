package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record ClientboundFarmStationSoulPacket(Map<Identifier, FarmSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<ClientboundFarmStationSoulPacket> TYPE = new Type<>(EnderIO.id("farm_soul"));

    public static final StreamCodec<ByteBuf, ClientboundFarmStationSoulPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, FarmSoul.STREAM_CODEC),
            ClientboundFarmStationSoulPacket::map, ClientboundFarmStationSoulPacket::new);

    public ClientboundFarmStationSoulPacket(FriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readIdentifier,
                buff -> new FarmSoul.SoulData(buff.readIdentifier(), buff.readFloat(), buff.readInt(),
                        buff.readFloat())));
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
