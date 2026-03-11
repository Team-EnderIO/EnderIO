package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundRemoveConduitFacadePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundRemoveConduitFacadePacket> TYPE = new Type<>(EnderIO.rl("remove_facade"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundRemoveConduitFacadePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ServerboundRemoveConduitFacadePacket::pos,
        ServerboundRemoveConduitFacadePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
