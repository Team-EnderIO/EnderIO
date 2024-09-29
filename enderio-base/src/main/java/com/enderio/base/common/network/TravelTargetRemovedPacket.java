package com.enderio.base.common.network;

import com.enderio.EnderIOBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TravelTargetRemovedPacket(BlockPos pos) implements CustomPacketPayload {

    public static Type<TravelTargetRemovedPacket> TYPE = new Type<>(EnderIOBase.loc("remove_travel_target"));

    public static StreamCodec<ByteBuf, TravelTargetRemovedPacket> STREAM_CODEC =
        BlockPos.STREAM_CODEC.map(TravelTargetRemovedPacket::new, TravelTargetRemovedPacket::pos);

    public TravelTargetRemovedPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
