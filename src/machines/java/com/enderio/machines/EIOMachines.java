package com.enderio.machines;

import com.enderio.EnderIO;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.data.EIODataProvider;
import com.enderio.machines.client.rendering.travel.TravelAnchorRenderer;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachinePackets;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.EnderIOMachinesSelfIntegration;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.GhostMachineSlot;
import com.enderio.machines.common.menu.MachineSlot;
import com.enderio.machines.common.menu.PreviewMachineSlot;
import com.enderio.machines.common.tag.MachineTags;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.enderio.machines.data.advancements.MachinesAdvancementGenerator;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.enderio.machines.data.recipes.EnchanterRecipeProvider;
import com.enderio.machines.data.recipes.MachineRecipeProvider;
import com.enderio.machines.data.recipes.PaintingRecipeProvider;
import com.enderio.machines.data.recipes.SagMillRecipeProvider;
import com.enderio.machines.data.recipes.SlicingRecipeProvider;
import com.enderio.machines.data.recipes.SoulBindingRecipeProvider;
import com.enderio.machines.data.recipes.TankRecipeProvider;
import com.enderio.machines.data.souldata.SoulDataProvider;
import com.enderio.machines.data.tag.MachineEntityTypeTagsProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOMachines {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Register machine config
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        // Get event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Perform classloads for everything so things are registered.
        MachineBlocks.register(modEventBus);
        MachineBlockEntities.register(modEventBus);
        MachineMenus.register(modEventBus);
        MachinePackets.register();

        MachineLang.register();
        MachineRecipes.register(modEventBus);
        MachineTags.register();

        IntegrationManager.addIntegration(EnderIOMachinesSelfIntegration.INSTANCE);
        TravelRegistry.addTravelEntry(EnderIO.loc("travel_anchor"), AnchorTravelTarget::new, () -> TravelAnchorRenderer::new);
    }

    @SubscribeEvent
    public static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("inventorysorter", "slotblacklist", MachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", GhostMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", PreviewMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", EnchanterMenu.EnchanterOutputMachineSlot.class::getName);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        EIODataProvider provider = new EIODataProvider("machines");

        provider.addSubProvider(event.includeServer(), new MachineRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new AlloyRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new EnchanterRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new SagMillRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new SlicingRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new SoulBindingRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new TankRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new PaintingRecipeProvider(packOutput, completablefuture));
        provider.addSubProvider(event.includeServer(), new SoulDataProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new MachineEntityTypeTagsProvider(packOutput, completablefuture, event.getExistingFileHelper()));

        generator.addProvider(true, provider);
        provider.addSubProvider(event.includeServer(), new AdvancementProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper(),
            List.of(new MachinesAdvancementGenerator())));
    }
}
