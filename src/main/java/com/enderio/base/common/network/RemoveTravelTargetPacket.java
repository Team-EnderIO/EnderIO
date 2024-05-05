package com.enderio.base.common.network;

import com.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemoveTravelTargetPacket(BlockPos pos) implements CustomPacketPayload {

    public static Type<RemoveTravelTargetPacket> TYPE = new Type<>(EnderIO.loc("remove_travel_target"));

    public static StreamCodec<ByteBuf, RemoveTravelTargetPacket> STREAM_CODEC =
        BlockPos.STREAM_CODEC.map(RemoveTravelTargetPacket::new, RemoveTravelTargetPacket::pos);

    public RemoveTravelTargetPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
