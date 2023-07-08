package com.enderio.armory;

import com.enderio.EnderIO;
import com.enderio.base.data.EIODataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOArmory {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @SubscribeEvent
    public static void onData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        EIODataProvider provider = new EIODataProvider("armory");

        event.getGenerator().addProvider(true, provider);
    }
}
