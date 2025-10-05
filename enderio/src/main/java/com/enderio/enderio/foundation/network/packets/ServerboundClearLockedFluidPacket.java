package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundClearLockedFluidPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundClearLockedFluidPacket> TYPE = new Type<>(EnderIO.rl("clear_locked_fluid"));

    public static final StreamCodec<ByteBuf, ServerboundClearLockedFluidPacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(ServerboundClearLockedFluidPacket::new, ServerboundClearLockedFluidPacket::pos);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
