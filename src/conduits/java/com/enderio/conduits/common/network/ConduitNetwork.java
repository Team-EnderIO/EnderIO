package com.enderio.conduits.common.network;

import com.enderio.core.EnderCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConduitNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event
            .registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToServer(C2SSetConduitConnectionState.TYPE, C2SSetConduitConnectionState.STREAM_CODEC,
            ConduitClientPayloadHandler.getInstance()::handleConduitConnectionState);

        registrar.playToServer(C2SSetConduitExtendedData.TYPE, C2SSetConduitExtendedData.STREAM_CODEC,
            ConduitClientPayloadHandler.getInstance()::handleConduitExtendedData);

        registrar.playToServer(DoubleChannelPacket.TYPE, DoubleChannelPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleDoubleChannelFilter);

        registrar.playToServer(TimerFilterPacket.TYPE, TimerFilterPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleTimerFilter);

        registrar.playToServer(CountFilterPacket.TYPE, CountFilterPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleCountFilter);
    }

}
