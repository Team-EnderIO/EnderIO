package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.souldata.SolarSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record ClientboundSolarSoulPacket(Map<Identifier, SolarSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<ClientboundSolarSoulPacket> TYPE = new Type<>(EnderIO.id("solar_soul"));

    public static final StreamCodec<ByteBuf, ClientboundSolarSoulPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, SolarSoul.STREAM_CODEC),
        ClientboundSolarSoulPacket::map,
        ClientboundSolarSoulPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
