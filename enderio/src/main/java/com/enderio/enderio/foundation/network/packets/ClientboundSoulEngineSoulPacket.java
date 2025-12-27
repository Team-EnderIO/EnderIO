package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record ClientboundSoulEngineSoulPacket(Map<Identifier, EngineSoul.SoulData> map)
    implements CustomPacketPayload {

    public static final Type<ClientboundSoulEngineSoulPacket> TYPE = new Type<>(EnderIO.rl("soul_engine_soul"));

    public static final StreamCodec<ByteBuf, ClientboundSoulEngineSoulPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, EngineSoul.STREAM_CODEC),
        ClientboundSoulEngineSoulPacket::map,
        ClientboundSoulEngineSoulPacket::new
    );

    public ClientboundSoulEngineSoulPacket(FriendlyByteBuf buf) {
        this(
            buf.readMap(FriendlyByteBuf::readIdentifier, buff ->
                new EngineSoul.SoulData(buff.readIdentifier(), buff.readUtf(), buff.readInt(), buff.readInt())
            )
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
