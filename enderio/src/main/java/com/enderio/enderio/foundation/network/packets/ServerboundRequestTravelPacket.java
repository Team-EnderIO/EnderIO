package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundRequestTravelPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundRequestTravelPacket> TYPE = new Type<>(EnderIO.id("request_travel"));

    public static final StreamCodec<ByteBuf, ServerboundRequestTravelPacket> STREAM_CODEC =
        BlockPos.STREAM_CODEC.map(ServerboundRequestTravelPacket::new, ServerboundRequestTravelPacket::pos);

    public ServerboundRequestTravelPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
