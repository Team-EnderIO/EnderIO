package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundDestroyEntireConduitBundlePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundDestroyEntireConduitBundlePacket> TYPE = new Type<>(EnderIO.rl("destroy_bundle"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundDestroyEntireConduitBundlePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ServerboundDestroyEntireConduitBundlePacket::pos,
        ServerboundDestroyEntireConduitBundlePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
