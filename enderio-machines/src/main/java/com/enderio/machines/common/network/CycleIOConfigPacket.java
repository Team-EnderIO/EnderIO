package com.enderio.machines.common.network;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CycleIOConfigPacket(BlockPos pos, Direction side) implements CustomPacketPayload {

    public static final Type<CycleIOConfigPacket> TYPE = new Type<>(EnderIO.loc("cycle_io_config"));

    public static final StreamCodec<ByteBuf, CycleIOConfigPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            CycleIOConfigPacket::pos, Direction.STREAM_CODEC, CycleIOConfigPacket::side, CycleIOConfigPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
