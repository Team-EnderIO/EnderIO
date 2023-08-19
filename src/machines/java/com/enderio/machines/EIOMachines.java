package com.enderio.machines;

import com.enderio.EnderIO;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.data.EIODataProvider;
import com.enderio.machines.client.rendering.travel.TravelAnchorRenderer;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.*;
import com.enderio.machines.common.integrations.EnderIOMachinesSelfIntegration;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.GhostMachineSlot;
import com.enderio.machines.common.menu.MachineSlot;
import com.enderio.machines.common.menu.PreviewMachineSlot;
import com.enderio.machines.common.network.MachineNetwork;
import com.enderio.machines.common.tag.MachineTags;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import com.enderio.machines.data.advancements.MachinesAdvancementGenerator;
import com.enderio.machines.data.recipes.*;
import com.enderio.machines.data.souldata.SoulDataProvider;
import com.enderio.machines.data.tag.MachineEntityTypeTagsProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.MissingMappingsEvent;

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

        // Perform classloads for everything so things are registered.
        MachineBlocks.register();
        MachineBlockEntities.register();
        MachineMenus.register();
        MachinePackets.register();

        MachineLang.register();
        MachineRecipes.register();
        MachineTags.register();
        MachineNetwork.networkInit();

        // Remap
        MinecraftForge.EVENT_BUS.addListener(EIOMachines::missingMappings);

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

        provider.addSubProvider(event.includeServer(), new MachineRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new AlloyRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new EnchanterRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new SagMillRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new SlicingRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new SoulBindingRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new TankRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new PaintingRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new SoulDataProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new MachineEntityTypeTagsProvider(packOutput, completablefuture, event.getExistingFileHelper()));

        generator.addProvider(true, provider);
        provider.addSubProvider(event.includeServer(), new ForgeAdvancementProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper(),
            List.of(new MachinesAdvancementGenerator())));
    }

    //TODO Remove later on when during beta/release.
    public static void missingMappings(MissingMappingsEvent event) {
        event.getMappings(Registries.BLOCK, EnderIO.MODID).forEach(mapping -> {
            if (mapping.getKey().equals(EnderIO.loc("simple_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC).get());
            } else if (mapping.getKey().equals(EnderIO.loc("basic_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC).get());
            } else if (mapping.getKey().equals(EnderIO.loc("advanced_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING).get());
            } else if (mapping.getKey().equals(EnderIO.loc("vibrant_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT).get());
            }
        });

        event.getMappings(Registries.ITEM, EnderIO.MODID).forEach(mapping -> {
            if (mapping.getKey().equals(EnderIO.loc("simple_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC).get().asItem());
            } else if (mapping.getKey().equals(EnderIO.loc("basic_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC).get().asItem());
            } else if (mapping.getKey().equals(EnderIO.loc("advanced_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING).get().asItem());
            } else if (mapping.getKey().equals(EnderIO.loc("vibrant_photovoltaic_cell"))) {
                mapping.remap(MachineBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT).get().asItem());
            }
        });
    }
}
