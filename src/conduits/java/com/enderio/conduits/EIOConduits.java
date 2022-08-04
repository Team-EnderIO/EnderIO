package com.enderio.conduits;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOConduits {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        System.out.println("================ Conduits construct ==================");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ConduitTypes.CONDUIT_TYPES.register(bus);
//        EnderIO.registrate().item("test", Item::new).register();
    }
    // GatherDataEvent too!
}
