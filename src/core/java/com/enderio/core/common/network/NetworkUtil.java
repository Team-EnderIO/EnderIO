package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkUtil {
    /**
     * @deprecated Use {@link PacketDistributor#sendToPlayer(ServerPlayer, CustomPacketPayload, CustomPacketPayload...)} instead
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static <T extends CustomPacketPayload> void sendTo(T packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    /**
     * @deprecated Use {@link PacketDistributor#sendToServer(CustomPacketPayload, CustomPacketPayload...)} instead
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    /**
     * @deprecated Use {@link PacketDistributor#sendToPlayersTrackingChunk(ServerLevel, ChunkPos, CustomPacketPayload, CustomPacketPayload...)} instead
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static <T extends CustomPacketPayload> void sendToAllTracking(T packet, ServerLevel level, BlockPos pos) {
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), packet);
    }
}
