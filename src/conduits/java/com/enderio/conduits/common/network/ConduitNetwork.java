package com.enderio.conduits.common.network;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.ClientPayloadHandler;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.network.EmitParticlePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConduitNetwork {

    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event
            .registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.play(C2SSetConduitConnectionState.ID, C2SSetConduitConnectionState::new,
            handler -> handler
                .client(ConduitClientPayloadHandler.getInstance()::handleConduitConnectionState));

        registrar.play(C2SSetConduitExtendedData.ID, C2SSetConduitExtendedData::new,
            handler -> handler
                .client(ConduitClientPayloadHandler.getInstance()::handleConduitExtendedData));
    }

}
