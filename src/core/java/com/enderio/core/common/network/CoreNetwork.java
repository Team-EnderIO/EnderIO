package com.enderio.core.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class CoreNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    private static SimpleChannel CHANNEL;

    private static int packetId = 0;

    /**
     * **MUST** be called by EnderIO before any other networking code.
     */
    public static void networkInit() {
        // Create the network channel.
        CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("enderio", "network"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

        // Register core packets.
        registerPacket(new EmitParticlePacket.Handler(), EmitParticlePacket.class);
        registerPacket(new EmitParticlesPacket.Handler(), EmitParticlesPacket.class);
        registerPacket(new S2CDataSlotUpdate.Handler(), S2CDataSlotUpdate.class);
        registerPacket(new C2SDataSlotChange.Handler(), C2SDataSlotChange.class);
    }

    public static <P extends Packet> void sendToServer(P packet) {
        CHANNEL.sendToServer(packet);
    }

    public static <P extends Packet> void sendToPlayer(ServerPlayer player, P packet) {
        send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static <P extends Packet> void sendToTracking(LevelChunk chunk, P packet) {
        send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static <P extends Packet> void send(PacketDistributor.PacketTarget target, P packet) {
        CHANNEL.send(target, packet);
    }

    public static <P extends Packet> void registerPacket(Packet.PacketHandler<P> handler, Class<P> clazz) {
        CHANNEL.registerMessage(id(), clazz, handler::toNetwork, handler::fromNetwork, handler, handler.getDirection());
    }

    private static int id() {
        return packetId++;
    }
}
