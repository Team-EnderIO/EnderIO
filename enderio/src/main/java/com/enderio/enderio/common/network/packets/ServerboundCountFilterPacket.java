package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.DyeColor;

public record ServerboundCountFilterPacket(DyeColor channel1, int maxCount, int count, boolean active) implements CustomPacketPayload {

    public static final Type<ServerboundCountFilterPacket> TYPE = new Type<>(EnderIO.rl("count_filter_packet"));

    public static final StreamCodec<ByteBuf, ServerboundCountFilterPacket> STREAM_CODEC = StreamCodec.composite(
        DyeColor.STREAM_CODEC,
        ServerboundCountFilterPacket::channel1,
        ByteBufCodecs.VAR_INT,
        ServerboundCountFilterPacket::maxCount,
        ByteBufCodecs.VAR_INT,
        ServerboundCountFilterPacket::count,
        ByteBufCodecs.BOOL,
        ServerboundCountFilterPacket::active,
        ServerboundCountFilterPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
