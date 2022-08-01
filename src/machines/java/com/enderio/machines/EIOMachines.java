package com.enderio.machines;

import com.enderio.EnderIO;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.enderio.machines.data.recipes.EnchanterRecipeProvider;
import com.enderio.machines.data.recipes.SagMillRecipeProvider;
import com.enderio.machines.data.recipes.SlicingRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOMachines {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Register machine config
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        // Perform classloads for everything so things are registered.
        MachineBlocks.register();
        MachineBlockEntities.register();
        MachineMenus.register();
        MachineLang.register();
        MachineRecipes.register();
    }

    @SubscribeEvent
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
