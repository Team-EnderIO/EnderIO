package com.enderio.machines.common.network;

import com.enderio.EnderIOBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record VatMoveTankPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<VatMoveTankPacket> TYPE = new Type<>(EnderIOBase.loc("vat_move_tank"));

    public static StreamCodec<ByteBuf, VatMoveTankPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, VatMoveTankPacket::pos,
        VatMoveTankPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
