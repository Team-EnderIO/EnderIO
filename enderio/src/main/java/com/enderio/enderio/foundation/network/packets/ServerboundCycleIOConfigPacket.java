package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundCycleIOConfigPacket(BlockPos pos, Direction side) implements CustomPacketPayload {

    public static final Type<ServerboundCycleIOConfigPacket> TYPE = new Type<>(EnderIO.rl("cycle_io_config"));

    public static final StreamCodec<ByteBuf, ServerboundCycleIOConfigPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC,
            ServerboundCycleIOConfigPacket::pos, Direction.STREAM_CODEC, ServerboundCycleIOConfigPacket::side, ServerboundCycleIOConfigPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
