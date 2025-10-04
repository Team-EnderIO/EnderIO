package com.enderio.enderio.common;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.common.compat.cctweaked.ComputerCraftCompat;
import com.enderio.enderio.common.compat.ftb_ultimine.FTBUltimineCompat;
import com.enderio.enderio.common.compat.inventorysorter.InventorySorterCompat;
import com.enderio.enderio.common.compat.laserio.LaserIOCompat;
import com.enderio.enderio.common.config.BaseConfig;
import com.enderio.enderio.common.config.BaseConfigLang;
import com.enderio.enderio.common.handlers.PlayerMovementHandler;
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
import com.enderio.enderio.common.integrations.Integrations;
import com.enderio.enderio.common.item.tool.SoulVialItem;
import com.enderio.enderio.common.lang.EIOEnumLang;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.common.tag.EIOTags;
import com.enderio.enderio.data.EIODataProvider;
import com.enderio.enderio.data.loot.ChestLootProvider;
import com.enderio.enderio.machines.common.lang.MachineLang;
import com.enderio.regilite.Regilite;
import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

        REGILITE.register(modEventBus);

        // Handle mod compat
        for (Map.Entry<String, Consumer<IEventBus>> entry : MOD_INTEGRATIONS.entrySet()) {
            logger.debug("Activating mod integration for {}", entry.getKey());
            if (ModList.get().isLoaded(entry.getKey())) {
                entry.getValue().accept(modEventBus);
            }
        }

        // Run datagen after registrate is finished.
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);
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

    public void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("base");

        provider.addSubProvider(event.includeServer(),
                new LootTableProvider(packOutput, Collections.emptySet(), List
                        .of(new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)),
                        lookupProvider));
        generator.addProvider(true, provider);
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
