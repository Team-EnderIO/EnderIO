package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelEndabledUpdatePacket;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.core.EnderCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ArmoryNetwork {

    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(EnderCore.MOD_ID).versioned(PROTOCOL_VERSION);

        registrar.playToServer(TravelEndabledUpdatePacket.TYPE, TravelEndabledUpdatePacket.STREAM_CODEC,
                TravelUpgrade::handleTravelEnabledPacket);
    }

}
