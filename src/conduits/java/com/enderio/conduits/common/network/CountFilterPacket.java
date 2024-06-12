package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.misc.ColorControl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CountFilterPacket(ColorControl channel1, int maxCount, int count, boolean active) implements CustomPacketPayload {

    public static Type<CountFilterPacket> TYPE = new Type<>(EnderIO.loc("count_filter_packet"));

    public static final StreamCodec<ByteBuf, CountFilterPacket> STREAM_CODEC = StreamCodec.composite(
        ColorControl.STREAM_CODEC,
        CountFilterPacket::channel1,
        ByteBufCodecs.VAR_INT,
        CountFilterPacket::maxCount,
        ByteBufCodecs.VAR_INT,
        CountFilterPacket::count,
        ByteBufCodecs.BOOL,
        CountFilterPacket::active,
        CountFilterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
