package com.enderio.enderio.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.data.EIODataProvider;
import com.enderio.enderio.machines.common.blocks.base.menu.GhostMachineSlot;
import com.enderio.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.enderio.machines.common.blocks.base.menu.PreviewMachineSlot;
import com.enderio.enderio.machines.common.blocks.enchanter.EnchanterMenu;
import com.enderio.enderio.machines.common.config.MachinesConfig;
import com.enderio.enderio.machines.common.config.MachinesConfigLang;
import com.enderio.enderio.machines.common.init.MachineAttachments;
import com.enderio.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.enderio.machines.common.init.MachineBlocks;
import com.enderio.enderio.machines.common.init.MachineDataComponents;
import com.enderio.enderio.machines.common.init.MachineMenus;
import com.enderio.enderio.machines.common.init.MachineRecipes;
import com.enderio.enderio.machines.common.init.MachineTravelTargets;
import com.enderio.enderio.machines.common.lang.MachineEnumLang;
import com.enderio.enderio.machines.common.lang.MachineLang;
import com.enderio.enderio.machines.common.tag.MachineTags;
import com.enderio.regilite.Regilite;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
@Mod(EnderIO.MOD_ID)
public class EnderIOMachines {
    public static final Regilite REGILITE = EnderIO.REGILITE;

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
    public static void addBuiltInPacks(final AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "data/enderio/datapacks/farming_station"),
                PackType.SERVER_DATA, MachineLang.FARMING_STATION_EXPERIMENT, PackSource.FEATURE, false,
                Pack.Position.TOP);

        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "data/enderio/datapacks/enderface"),
                PackType.SERVER_DATA, MachineLang.ENDERFACE_EXPERIMENT, PackSource.FEATURE, false, Pack.Position.TOP);

        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "data/enderio/datapacks/niard"),
                PackType.SERVER_DATA, MachineLang.NIARD_EXPERIMENT, PackSource.FEATURE, false, Pack.Position.TOP);
    }
}
