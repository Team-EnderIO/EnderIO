package com.enderio.conduits.common.network;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TimerFilterPacket(int ticks, int maxTicks) implements CustomPacketPayload {

    public static final Type<TimerFilterPacket> TYPE = new Type<>(EnderIO.loc("timer_filter_packet"));

    public static final StreamCodec<ByteBuf, TimerFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        TimerFilterPacket::ticks,
        ByteBufCodecs.VAR_INT,
        TimerFilterPacket::maxTicks,
        TimerFilterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
