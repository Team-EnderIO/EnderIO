package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundTravelTargetRemovedPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClientboundTravelTargetRemovedPacket> TYPE = new Type<>(EnderIO.rl("remove_travel_target"));

    public static final StreamCodec<ByteBuf, ClientboundTravelTargetRemovedPacket> STREAM_CODEC =
        BlockPos.STREAM_CODEC.map(ClientboundTravelTargetRemovedPacket::new, ClientboundTravelTargetRemovedPacket::pos);

    public ClientboundTravelTargetRemovedPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
