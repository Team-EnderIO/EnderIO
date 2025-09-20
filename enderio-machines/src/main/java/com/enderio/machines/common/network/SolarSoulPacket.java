package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.souldata.SolarSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record SolarSoulPacket(Map<ResourceLocation, SolarSoul.SoulData> map) implements CustomPacketPayload {

    public static final Type<SolarSoulPacket> TYPE = new Type<>(EnderIO.loc("solar_soul"));

    public static final StreamCodec<ByteBuf, SolarSoulPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, SolarSoul.STREAM_CODEC),
        SolarSoulPacket::map,
        SolarSoulPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
