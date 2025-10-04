package com.enderio.enderio;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.common.config.machines.MachinesConfig;
import com.enderio.enderio.common.config.machines.MachinesConfigLang;
import com.enderio.enderio.common.foundation.lang.MachineEnumLang;
import com.enderio.enderio.common.init.MachineAttachments;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineBlocks;
import com.enderio.enderio.common.init.MachineDataComponents;
import com.enderio.enderio.common.init.MachineMenus;
import com.enderio.enderio.common.init.MachineRecipes;
import com.enderio.enderio.common.init.MachineTravelTargets;
import com.enderio.enderio.compat.cctweaked.ComputerCraftCompat;
import com.enderio.enderio.compat.ftb_ultimine.FTBUltimineCompat;
import com.enderio.enderio.compat.inventorysorter.InventorySorterCompat;
import com.enderio.enderio.compat.laserio.LaserIOCompat;
import com.enderio.enderio.common.config.base.BaseConfig;
import com.enderio.enderio.common.config.base.BaseConfigLang;
import com.enderio.enderio.common.content.tools.hang_glider.PlayerMovementHandler;
import com.enderio.enderio.common.init.ConduitBlockEntities;
import com.enderio.enderio.common.init.ConduitBlocks;
import com.enderio.enderio.common.init.ConduitComponents;
import com.enderio.enderio.common.init.ConduitIngredientTypes;
import com.enderio.enderio.common.init.ConduitItems;
import com.enderio.enderio.common.init.ConduitLang;
import com.enderio.enderio.common.init.ConduitMenus;
import com.enderio.enderio.common.init.ConduitTypes;
import com.enderio.enderio.common.init.EIOAttachments;
import com.enderio.enderio.common.init.EIOBlockEntities;
import com.enderio.enderio.common.init.EIOBlocks;
import com.enderio.enderio.common.init.EIOCreativeTabs;
import com.enderio.enderio.common.init.EIOCriterions;
import com.enderio.enderio.common.init.EIODataComponents;
import com.enderio.enderio.common.init.EIOEntities;
import com.enderio.enderio.common.init.EIOFluids;
import com.enderio.enderio.common.init.EIOIngredientTypes;
import com.enderio.enderio.common.init.EIOItems;
import com.enderio.enderio.common.init.EIOLootModifiers;
import com.enderio.enderio.common.init.EIOMenus;
import com.enderio.enderio.common.init.EIOParticles;
import com.enderio.enderio.common.init.EIORecipes;
import com.enderio.enderio.common.foundation.integrations.Integrations;
import com.enderio.enderio.common.content.vials.SoulVialItem;
import com.enderio.enderio.common.foundation.lang.EIOEnumLang;
import com.enderio.enderio.common.foundation.lang.EIOLang;
import com.enderio.enderio.common.foundation.tag.EIOTags;
import com.enderio.enderio.common.foundation.lang.MachineLang;
import com.enderio.regilite.Regilite;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;

@Mod(EnderIO.MOD_ID)
public class EnderIO {
    public static final String MOD_ID = EnderIOAPI.MOD_ID;

    public static final Regilite REGILITE = new Regilite(EnderIO.MOD_ID);

    public static IEventBus modEventBus;
    public static ModContainer modContainer;

    public static ResourceLocation rl(String path) {
        return EnderIOAPI.rl(path);
    }

    private static final Map<String, Consumer<IEventBus>> MOD_INTEGRATIONS = Map.ofEntries(
        Map.entry("computercraft", eventBus -> ComputerCraftCompat.init()),
        Map.entry("ftbultimine", eventBus -> FTBUltimineCompat.init()),
        Map.entry("laserio", LaserIOCompat::init),
        Map.entry("inventorysorter", InventorySorterCompat::init)
    );

    private final Logger logger = LogUtils.getLogger();

    public EnderIO(IEventBus modEventBus, ModContainer modContainer) {
        EnderIO.modEventBus = modEventBus;
        EnderIO.modContainer = modContainer;

        // Ensure the enderio config subdirectory is present.
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(EnderIO.MOD_ID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register config files
        modContainer.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");
        BaseConfigLang.register();

        // Perform initialization and registration for everything so things are
        // registered.
        EIODataComponents.register(modEventBus);
        EIOCreativeTabs.register(modEventBus);
        EIOItems.register(modEventBus);
        EIOBlocks.register(modEventBus);
        EIOBlockEntities.register(modEventBus);
        EIOFluids.register(modEventBus);
        EIOTags.register();
        EIOMenus.register(modEventBus);
        EIOLang.register();
        EIOEnumLang.register();
        EIORecipes.register(modEventBus);
        EIOLootModifiers.register(modEventBus);
        EIOParticles.register(modEventBus);
        EIOEntities.register(modEventBus);
        EIOAttachments.register(modEventBus);
        EIOCriterions.register(modEventBus);
        EIOIngredientTypes.register(modEventBus);

        ConduitTypes.register(modEventBus);
        ConduitBlockEntities.register(modEventBus);
        ConduitMenus.register(modEventBus);
        ConduitBlocks.register(modEventBus);
        ConduitItems.register(modEventBus);
        ConduitComponents.register(modEventBus);
        ConduitIngredientTypes.register(modEventBus);
        ConduitLang.register();

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

        REGILITE.register(modEventBus);

        // Handle mod compat
        for (Map.Entry<String, Consumer<IEventBus>> entry : MOD_INTEGRATIONS.entrySet()) {
            logger.debug("Activating mod integration for {}", entry.getKey());
            if (ModList.get().isLoaded(entry.getKey())) {
                entry.getValue().accept(modEventBus);
            }
        }

        // Run datagen after registrate is finished.
        modEventBus.addListener(SoulVialItem::onCommonSetup);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::addBuiltInPacks);

        Integrations.register();

        NeoForge.EVENT_BUS.addListener(PlayerMovementHandler::onPlayerTick);
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(EnderIORegistries.TRAVEL_TARGET_TYPES);
        event.register(EnderIORegistries.TRAVEL_TARGET_SERIALIZERS);
        event.register(EnderIORegistries.CONDUIT_TYPE);
        event.register(EnderIORegistries.CONDUIT_DATA_TYPE);
        event.register(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE);
        event.register(EnderIORegistries.CONDUIT_NODE_DATA_TYPE);
        event.register(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_TYPE);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(EnderIORegistries.Keys.CONDUIT, Conduit.DIRECT_CODEC, Conduit.DIRECT_CODEC);
    }

    public void addBuiltInPacks(final AddPackFindersEvent event) {
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
