package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundTimerFilterPacket(int ticks, int maxTicks) implements CustomPacketPayload {

    public static final Type<ServerboundTimerFilterPacket> TYPE = new Type<>(EnderIO.rl("timer_filter_packet"));

    public static final StreamCodec<ByteBuf, ServerboundTimerFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        ServerboundTimerFilterPacket::ticks,
        ByteBufCodecs.VAR_INT,
        ServerboundTimerFilterPacket::maxTicks,
        ServerboundTimerFilterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
