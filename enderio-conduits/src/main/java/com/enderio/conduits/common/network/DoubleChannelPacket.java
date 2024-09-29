package com.enderio.conduits.common.network;

import com.enderio.EnderIOBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.DyeColor;

public record DoubleChannelPacket(DyeColor channel1, DyeColor channel2) implements CustomPacketPayload {

    public static Type<DoubleChannelPacket> TYPE = new Type<>(EnderIOBase.loc("double_channel_packet"));

    public static final StreamCodec<ByteBuf, DoubleChannelPacket> STREAM_CODEC = StreamCodec.composite(
        DyeColor.STREAM_CODEC,
        DoubleChannelPacket::channel1,
        DyeColor.STREAM_CODEC,
        DoubleChannelPacket::channel2,
        DoubleChannelPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
