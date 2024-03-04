package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkUtil {
    public static <T extends CustomPacketPayload> void sendTo(T packet, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.SERVER.noArg().send(packet);
    }

    public static <T extends CustomPacketPayload> void sendToAllTracking(T packet, Level level, BlockPos pos) {
        PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(pos)).send(packet);
    }

    public static <T extends CustomPacketPayload> void sendToDimension(T packet, ResourceKey<Level> dimensionKey) {
        PacketDistributor.DIMENSION.with(dimensionKey).send(packet);
    }
}
