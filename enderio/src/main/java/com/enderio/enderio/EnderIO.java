package com.enderio.enderio;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.compat.cctweaked.ComputerCraftCompat;
import com.enderio.enderio.compat.ftb_ultimine.FTBUltimineCompat;
import com.enderio.enderio.compat.inventorysorter.InventorySorterCompat;
import com.enderio.enderio.compat.laserio.LaserIOCompat;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.config.conduits.ConduitsConfig;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.integrations.Integrations;
import com.enderio.enderio.init.EIOAttachments;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduitTypes;
import com.enderio.enderio.init.EIOCreativeTabs;
import com.enderio.enderio.init.EIOCriterions;
import com.enderio.enderio.init.EIOEntities;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOIngredientTypes;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIOLootModifiers;
import com.enderio.enderio.init.EIOMenus;
import com.enderio.enderio.init.EIOParticles;
import com.enderio.enderio.init.EIORecipes;
import com.enderio.enderio.init.EIOTravelTargets;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;

@Mod(EnderIO.MOD_ID)
public class EnderIO {
    public static final String MOD_ID = EnderIOAPI.MOD_ID;

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
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, ConduitsConfig.COMMON_SPEC, "enderio/conduits-common.toml");

        // Perform initialization and registration for everything so things are
        // registered.
        EIOCreativeTabs.register(modEventBus);
        EIOItems.register(modEventBus);
        EIOBlocks.register(modEventBus);
        EIOBlockEntities.register(modEventBus);
        EIOFluids.register(modEventBus);
        EIOMenus.register(modEventBus);
        EIORecipes.register(modEventBus);
        EIOLootModifiers.register(modEventBus);
        EIOParticles.register(modEventBus);
        EIOEntities.register(modEventBus);
        EIOAttachments.register(modEventBus);
        EIOCriterions.register(modEventBus);
        EIOIngredientTypes.register(modEventBus);
        EIOConduitTypes.register(modEventBus);
        EIOTravelTargets.register(modEventBus);

        // Handle mod compat
        for (Map.Entry<String, Consumer<IEventBus>> entry : MOD_INTEGRATIONS.entrySet()) {
            logger.debug("Activating mod integration for {}", entry.getKey());
            if (ModList.get().isLoaded(entry.getKey())) {
                entry.getValue().accept(modEventBus);
            }
        }

        // Run datagen after registrate is finished.
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::addBuiltInPacks);

        Integrations.register();
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
            new ResourceLocation(EnderIO.MOD_ID, "data/enderio/datapacks/farming_station"),
            PackType.SERVER_DATA, MachinesLang.FARMING_STATION_EXPERIMENT, PackSource.FEATURE, false,
            Pack.Position.TOP);

        event.addPackFinders(
            new ResourceLocation(EnderIO.MOD_ID, "data/enderio/datapacks/enderface"),
            PackType.SERVER_DATA, MachinesLang.ENDERFACE_EXPERIMENT, PackSource.FEATURE, false, Pack.Position.TOP);

        event.addPackFinders(
            new ResourceLocation(EnderIO.MOD_ID, "data/enderio/datapacks/niard"),
            PackType.SERVER_DATA, MachinesLang.NIARD_EXPERIMENT, PackSource.FEATURE, false, Pack.Position.TOP);
    }
}
