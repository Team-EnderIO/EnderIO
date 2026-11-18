package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent when the client performs an extra jump in mid-air, so the server can validate it and reset fall distance.
 */
public record ServerboundDoubleJumpPacket() implements CustomPacketPayload {

    public static final Type<ServerboundDoubleJumpPacket> TYPE = new Type<>(EnderIO.rl("double_jump"));

    public static final StreamCodec<ByteBuf, ServerboundDoubleJumpPacket> STREAM_CODEC =
        StreamCodec.unit(new ServerboundDoubleJumpPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
