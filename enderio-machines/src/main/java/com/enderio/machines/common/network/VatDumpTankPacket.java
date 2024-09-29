package com.enderio.machines.common.network;

import com.enderio.EnderIOBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record VatDumpTankPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<VatDumpTankPacket> TYPE = new Type<>(EnderIOBase.loc("vat_dump_tank"));

    public static StreamCodec<ByteBuf, VatDumpTankPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, VatDumpTankPacket::pos,
        VatDumpTankPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
