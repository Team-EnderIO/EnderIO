package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.souldata.EngineSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record SoulEngineSoulPacket(Map<ResourceLocation, EngineSoul.SoulData> map)
    implements CustomPacketPayload {

    public static final Type<SoulEngineSoulPacket> TYPE = new Type<>(EnderIO.loc("soul_engine_soul"));

    public static final StreamCodec<ByteBuf, SoulEngineSoulPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, EngineSoul.STREAM_CODEC),
        SoulEngineSoulPacket::map,
        SoulEngineSoulPacket::new
    );

    public SoulEngineSoulPacket(FriendlyByteBuf buf) {
        this(
            buf.readMap(FriendlyByteBuf::readResourceLocation, buff ->
                new EngineSoul.SoulData(buff.readResourceLocation(), buff.readUtf(), buff.readInt(), buff.readInt())
            )
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
