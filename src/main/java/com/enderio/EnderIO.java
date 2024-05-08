package com.enderio;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.init.EIOCriterions;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOEntities;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIOLootModifiers;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.init.EIOParticles;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.EnderIOSelfIntegration;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.travel.TravelTargetSavedData;
import com.enderio.base.data.EIODataProvider;
import com.enderio.base.data.advancement.EIOAdvancementGenerator;
import com.enderio.base.data.loot.ChestLootProvider;
import com.enderio.base.data.loot.EIOLootModifiersProvider;
import com.enderio.base.data.loot.FireCraftingLootProvider;
import com.enderio.base.data.recipe.BlockRecipeProvider;
import com.enderio.base.data.recipe.FireCraftingRecipeProvider;
import com.enderio.base.data.recipe.GlassRecipeProvider;
import com.enderio.base.data.recipe.ItemRecipeProvider;
import com.enderio.base.data.recipe.MaterialRecipeProvider;
import com.enderio.base.data.tags.EIOBlockTagsProvider;
import com.enderio.base.data.tags.EIOEntityTagsProvider;
import com.enderio.base.data.tags.EIOFluidTagsProvider;
import com.enderio.base.data.tags.EIOItemTagsProvider;
import com.enderio.core.EnderCore;
import com.enderio.regilite.Regilite;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(EnderIO.MODID)
public class EnderIO {
    // The Mod ID. This is stored in EnderCore as it's the furthest source away but it ensures that it is constant across all source sets.
    public static final String MODID = EnderCore.MODID;

    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Regilite regilite = new Regilite(MODID);

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static IEventBus modEventBus;

    public EnderIO(IEventBus modEventBus, ModContainer modContainer) {
        EnderIO.modEventBus = modEventBus;

        // Ensure the enderio config subdirectory is present.
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(MODID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register config files
        modContainer.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");

        // Perform initialization and registration for everything so things are registered.
        EIODataComponents.register(modEventBus);
        EIOCreativeTabs.register(modEventBus);
        EIOItems.register(modEventBus);
        EIOBlocks.register(modEventBus);
        EIOBlockEntities.register(modEventBus);
        EIOFluids.register(modEventBus);
        EIOEnchantments.register(modEventBus);
        EIOTags.register();
        EIOMenus.register(modEventBus);
        EIOLang.register();
        EIORecipes.register(modEventBus);
        EIOLootModifiers.register(modEventBus);
        EIOParticles.register(modEventBus);
        EIOEntities.register(modEventBus);
        EIOAttachments.register(modEventBus);
        EIOCriterions.register(modEventBus);
        regilite.register(modEventBus);

        // Initialize API layers.
        TravelTargetSavedData.initAPI();

        // Run datagen after registrate is finished.
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);
        modEventBus.addListener(SoulVialItem::onCommonSetup);
        modEventBus.addListener(this::registerRegistries);
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
    }

    private void registerRegistries(NewRegistryEvent event) {
        // TODO: Do this in conduits?
        event.register(EnderIORegistries.CONDUIT_TYPES);
        event.register(EnderIORegistries.TRAVEL_TARGET_TYPES);
        event.register(EnderIORegistries.TRAVEL_TARGET_SERIALIZERS);
    }

    public void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("base");

        provider.addSubProvider(event.includeServer(), new MaterialRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new BlockRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new GlassRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new FireCraftingRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new EIOLootModifiersProvider(packOutput, lookupProvider));

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);
        provider.addSubProvider(event.includeServer(), new EIOItemTagsProvider(packOutput, lookupProvider, b.contentsGetter(), existingFileHelper));
        provider.addSubProvider(event.includeServer(), new EIOFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(), new EIOEntityTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(),
            new AdvancementProvider(packOutput, lookupProvider, existingFileHelper, List.of(new EIOAdvancementGenerator())));
        provider.addSubProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
            List.of(new LootTableProvider.SubProviderEntry(FireCraftingLootProvider::new, LootContextParamSets.EMPTY),
                new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)), lookupProvider));
        generator.addProvider(true, provider);
    }

    public static Regilite getRegilite() {
        return regilite;
    }
}
