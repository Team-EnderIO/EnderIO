package com.enderio.base.common.util;

import com.enderio.base.EnderIO;
import com.enderio.base.common.handler.travel.TravelRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber
public class EnderIMC {

    @SubscribeEvent
    public static void process(InterModProcessEvent event) {
        event.getIMCStream(name -> name.equals("travelRegistry")).forEach(EnderIMC::processTravelRegistry);
    }

//@SubscribeEvent
//public static void enqueue(InterModEnqueueEvent event) {
//    InterModComms.sendTo("enderio", "travelRegistry", () -> new TravelRegistry.TravelEntry<>(AnchorTravelTarget.SERIALIZED_NAME, AnchorTravelTarget::new, TravelAnchorRenderer::new));
//}

    public static void processTravelRegistry(InterModComms.IMCMessage comms) {
        Object o = comms.messageSupplier().get();
        if (o instanceof TravelRegistry.TravelEntry<?> travelEntry) {
            TravelRegistry.addTravelEntry(travelEntry);
        } else {
            EnderIO.LOGGER.warn(comms.senderModId() + " has tried to send an invalid object to enderio:travelRegistry");
        }
    }
}
