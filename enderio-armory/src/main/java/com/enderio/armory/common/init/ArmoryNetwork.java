package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.item.darksteel.upgrades.flight.FlightEnabledPacket;
import com.enderio.armory.common.item.darksteel.upgrades.flight.FlightUpgradeUtil;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisionEnabledPacket;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisionHandler;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUsePowerPacket;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelEndabledUpdatePacket;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID)
public class ArmoryNetwork {

    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(TravelEndabledUpdatePacket.TYPE, TravelEndabledUpdatePacket.STREAM_CODEC,
                TravelUpgrade::handleTravelEnabledPacket);

        registrar.playToServer(SpeedUsePowerPacket.TYPE, SpeedUsePowerPacket.STREAM_CODEC,
                SpeedUpgrade::handleEnergyUsePacket);

        registrar.playToServer(FlightEnabledPacket.TYPE, FlightEnabledPacket.STREAM_CODEC,
                FlightUpgradeUtil::handleActivePacket);

        registrar.playToServer(NightVisionEnabledPacket.TYPE, NightVisionEnabledPacket.STREAM_CODEC,
                NightVisionHandler.INST::handleActivePacket);
    }

}
