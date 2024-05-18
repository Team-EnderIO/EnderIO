package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CoreNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event
            .registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToClient(EmitParticlePacket.TYPE, EmitParticlePacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleEmitParticle);

        registrar.playToClient(EmitParticlesPacket.TYPE, EmitParticlesPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleEmitParticles);

        registrar.playToClient(ServerboundCDataSlotUpdate.TYPE, ServerboundCDataSlotUpdate.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleDataSlotUpdate);

        registrar.playToServer(ClientboundDataSlotChange.TYPE, ClientboundDataSlotChange.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleDataSlotChange);
    }
}
