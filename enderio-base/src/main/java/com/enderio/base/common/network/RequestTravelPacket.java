package com.enderio.base.common.network;

import com.enderio.EnderIOBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestTravelPacket(BlockPos pos) implements CustomPacketPayload {

    public static Type<RequestTravelPacket> TYPE = new Type<>(EnderIOBase.loc("request_travel"));

    public static StreamCodec<ByteBuf, RequestTravelPacket> STREAM_CODEC =
        BlockPos.STREAM_CODEC.map(RequestTravelPacket::new, RequestTravelPacket::pos);

    public RequestTravelPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
