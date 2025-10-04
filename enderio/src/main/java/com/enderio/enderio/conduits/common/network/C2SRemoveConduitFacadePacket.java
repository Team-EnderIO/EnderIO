package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SRemoveConduitFacadePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<C2SRemoveConduitFacadePacket> TYPE = new Type<>(EnderIO.rl("remove_facade"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRemoveConduitFacadePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SRemoveConduitFacadePacket::pos,
        C2SRemoveConduitFacadePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
