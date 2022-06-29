package com.enderio.machines;

import com.enderio.EnderIO;
import com.enderio.machines.common.init.*;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.enderio.machines.data.recipes.EnchanterRecipeProvider;
import com.enderio.machines.data.recipes.SagMillRecipeProvider;
import com.enderio.machines.data.recipes.SlicingRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOMachines {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Perform classloads for everything so things are registered.
        MachineBlocks.register();
        MachineBlockEntities.register();
        MachineMenus.register();
        MachineLang.register();
        MachineRecipes.register();
        MachineCapacitorKeys.register();

        // Run datagen after registrate
//        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), new AlloyRecipeProvider(generator));
            generator.addProvider(event.includeServer(), new EnchanterRecipeProvider(generator));
            generator.addProvider(event.includeServer(), new SagMillRecipeProvider(generator));
            generator.addProvider(event.includeServer(), new SlicingRecipeProvider(generator));
        }
    }
}
