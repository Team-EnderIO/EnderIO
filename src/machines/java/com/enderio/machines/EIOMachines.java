package com.enderio.machines;

import com.enderio.EnderIO;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.machines.client.rendering.travel.TravelAnchorRenderer;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.EnderIOMachinesSelfIntegration;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.enderio.machines.data.recipes.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import static com.enderio.EnderIO.loc;

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

        IntegrationManager.addIntegration(EnderIOMachinesSelfIntegration.INSTANCE);
        TravelRegistry.addTravelEntry(loc("travel_anchor"), AnchorTravelTarget::new, TravelAnchorRenderer::new);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new MachineRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new AlloyRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new EnchanterRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new SagMillRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new SlicingRecipeProvider(generator));
    }
}
