package com.enderio.machines;

import com.enderio.EnderIOBase;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.data.EIODataProvider;
import com.enderio.machines.common.blocks.base.menu.GhostMachineSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PreviewMachineSlot;
import com.enderio.machines.common.blocks.enchanter.EnchanterMenu;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.config.MachinesConfigLang;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.init.MachineTravelTargets;
import com.enderio.machines.common.integrations.EnderIOMachinesSelfIntegration;
import com.enderio.machines.common.lang.MachineEnumLang;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.tag.MachineTags;
import com.enderio.machines.data.advancements.MachinesAdvancementGenerator;
import com.enderio.machines.data.reagentdata.ReagentDataProvider;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.enderio.machines.data.recipes.EnchanterRecipeProvider;
import com.enderio.machines.data.recipes.FermentingRecipeProvider;
import com.enderio.machines.data.recipes.MachineRecipeProvider;
import com.enderio.machines.data.recipes.PaintingRecipeProvider;
import com.enderio.machines.data.recipes.SagMillRecipeProvider;
import com.enderio.machines.data.recipes.SlicingRecipeProvider;
import com.enderio.machines.data.recipes.SoulBindingRecipeProvider;
import com.enderio.machines.data.recipes.TankRecipeProvider;
import com.enderio.machines.data.souldata.SoulDataProvider;
import com.enderio.machines.data.tag.MachineEntityTypeTagsProvider;
import com.enderio.regilite.Regilite;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@Mod(EnderIOMachines.MODULE_MOD_ID)
public class EnderIOMachines {
    public static final String MODULE_MOD_ID = "enderio_machines";
    public static final String REGISTRY_NAMESPACE = EnderIOBase.REGISTRY_NAMESPACE;

    public static Regilite REGILITE = new Regilite(EnderIOBase.REGISTRY_NAMESPACE);

    public EnderIOMachines(IEventBus modEventBus, ModContainer modContainer) {
        // Register machine config
        modContainer.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        MachineDataComponents.register(modEventBus);
        MachineTravelTargets.register(modEventBus);
        MachineBlocks.register(modEventBus);
        MachineBlockEntities.register(modEventBus);
        MachineMenus.register(modEventBus);
        MachineRecipes.register(modEventBus);
        MachineAttachments.register(modEventBus);

        MachineLang.register();
        MachinesConfigLang.register();
        MachineEnumLang.register();
        MachineTags.register();

        REGILITE.register(modEventBus);

        IntegrationManager.addIntegration(EnderIOMachinesSelfIntegration.INSTANCE);
    }

    @SubscribeEvent
    public static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("inventorysorter", "slotblacklist", MachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", GhostMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", PreviewMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist",
                EnchanterMenu.EnchanterOutputMachineSlot.class::getName);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture
                .supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        EIODataProvider provider = new EIODataProvider("machines");

        provider.addSubProvider(event.includeServer(), new MachineRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new AlloyRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new EnchanterRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new FermentingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SagMillRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SlicingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SoulBindingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new TankRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new PaintingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new SoulDataProvider(packOutput));
        provider.addSubProvider(event.includeServer(),
                new MachineEntityTypeTagsProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        provider.addSubProvider(event.includeServer(),
                new ReagentDataProvider(packOutput, lookupProvider, event.getExistingFileHelper()));

        generator.addProvider(true, provider);
        provider.addSubProvider(event.includeServer(), new AdvancementProvider(packOutput, event.getLookupProvider(),
                event.getExistingFileHelper(), List.of(new MachinesAdvancementGenerator())));
    }
}
