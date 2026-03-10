package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = EnderCore.MOD_ID)
public class CoreNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        EnderCore.loc("main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(EmitParticlePacket.TYPE, EmitParticlePacket.STREAM_CODEC,
                ClientPayloadHandler.getInstance()::handleEmitParticle);

        registrar.playToClient(EmitParticlesPacket.TYPE, EmitParticlesPacket.STREAM_CODEC,
                ClientPayloadHandler.getInstance()::handleEmitParticles);

        registrar.playToClient(ServerboundCDataSlotUpdate.TYPE, ServerboundCDataSlotUpdate.STREAM_CODEC,
                ClientPayloadHandler.getInstance()::handleDataSlotUpdate);

        registrar.playToServer(ClientboundDataSlotChange.TYPE, ClientboundDataSlotChange.STREAM_CODEC,
                ServerPayloadHandler.getInstance()::handleDataSlotChange);

        registrar.playToClient(ClientboundSyncSlotDataPacket.TYPE, ClientboundSyncSlotDataPacket.STREAM_CODEC,
                ClientPayloadHandler.getInstance()::handleSyncSlotDataPacket);

        registrar.playToServer(ServerboundSetSyncSlotDataPacket.TYPE, ServerboundSetSyncSlotDataPacket.STREAM_CODEC,
                ServerPayloadHandler.getInstance()::handleSetSyncSlotDataPacket);
    }
}
