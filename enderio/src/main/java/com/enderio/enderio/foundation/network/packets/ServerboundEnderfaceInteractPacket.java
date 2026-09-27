package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public record ServerboundEnderfaceInteractPacket(BlockHitResult hitResult) implements CustomPacketPayload {
    public static final Type<ServerboundEnderfaceInteractPacket> TYPE = new Type<>(EnderIO.id("enderface_interact"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundEnderfaceInteractPacket> STREAM_CODEC = StreamCodec.composite(
        BlockHitResult.STREAM_CODEC, ServerboundEnderfaceInteractPacket::hitResult,
        ServerboundEnderfaceInteractPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
