package com.enderio.conduits.common.network;

import com.enderio.conduits.EnderIOConduits;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID)
public class ConduitNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(DoubleChannelPacket.TYPE, DoubleChannelPacket.STREAM_CODEC,
                ConduitServerPayloadHandler.getInstance()::handleDoubleChannelFilter);

        registrar.playToServer(TimerFilterPacket.TYPE, TimerFilterPacket.STREAM_CODEC,
                ConduitServerPayloadHandler.getInstance()::handleTimerFilter);

        registrar.playToServer(CountFilterPacket.TYPE, CountFilterPacket.STREAM_CODEC,
                ConduitServerPayloadHandler.getInstance()::handleCountFilter);

        registrar.playToServer(C2SClearLockedFluidPacket.TYPE, C2SClearLockedFluidPacket.STREAM_CODEC,
                ConduitServerPayloadHandler.getInstance()::handle);

        registrar.playToClient(S2CConduitExtraGuiDataPacket.TYPE, S2CConduitExtraGuiDataPacket.STREAM_CODEC,
                ConduitClientPayloadHandler.getInstance()::handle);

        registrar.playToClient(S2CConduitListPacket.TYPE, S2CConduitListPacket.STREAM_CODEC,
                ConduitClientPayloadHandler.getInstance()::handle);

        registrar.playBidirectional(SetConduitConnectionConfigPacket.TYPE,
                SetConduitConnectionConfigPacket.STREAM_CODEC, ConduitCommonPayloadHandler.getInstance()::handle);

        registrar.playToServer(C2SOpenConduitFilterMenu.TYPE, C2SOpenConduitFilterMenu.STREAM_CODEC,
                ConduitServerPayloadHandler.getInstance()::handle);
    }

}
