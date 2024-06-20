package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.misc.ColorControl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DoubleChannelPacket(ColorControl channel1, ColorControl channel2) implements CustomPacketPayload {

    public static Type<DoubleChannelPacket> TYPE = new Type<>(EnderIO.loc("double_channel_packet"));

    public static final StreamCodec<ByteBuf, DoubleChannelPacket> STREAM_CODEC = StreamCodec.composite(
        ColorControl.STREAM_CODEC,
        DoubleChannelPacket::channel1,
        ColorControl.STREAM_CODEC,
        DoubleChannelPacket::channel2,
        DoubleChannelPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
