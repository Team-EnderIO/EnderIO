package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.DyeColor;

public record ServerboundDoubleChannelPacket(DyeColor channel1, DyeColor channel2) implements CustomPacketPayload {

    public static final Type<ServerboundDoubleChannelPacket> TYPE = new Type<>(EnderIO.rl("double_channel_packet"));

    public static final StreamCodec<ByteBuf, ServerboundDoubleChannelPacket> STREAM_CODEC = StreamCodec.composite(
        DyeColor.STREAM_CODEC,
        ServerboundDoubleChannelPacket::channel1,
        DyeColor.STREAM_CODEC,
        ServerboundDoubleChannelPacket::channel2,
        ServerboundDoubleChannelPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
