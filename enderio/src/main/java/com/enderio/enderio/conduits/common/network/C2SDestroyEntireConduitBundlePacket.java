package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SDestroyEntireConduitBundlePacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<C2SDestroyEntireConduitBundlePacket> TYPE = new Type<>(EnderIO.rl("destroy_bundle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SDestroyEntireConduitBundlePacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SDestroyEntireConduitBundlePacket::pos,
        C2SDestroyEntireConduitBundlePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
