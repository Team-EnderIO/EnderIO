package com.enderio;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.registry.EnderIORegistries;
import com.enderio.base.common.compat.vanilla.SpawnEggSoulBindable;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.config.BaseConfigLang;
import com.enderio.base.common.filter.item.ItemFilterSlot;
import com.enderio.base.common.hangglider.PlayerMovementHandler;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.init.EIOCriterions;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOEntities;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOIngredientTypes;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIOLootModifiers;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.init.EIOParticles;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.Integrations;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.lang.EIOEnumLang;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.filter.fluid.FluidFilterSlot;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.EIODataProvider;
import com.enderio.base.data.advancement.EIOAdvancementGenerator;
import com.enderio.base.data.loot.ChestLootProvider;
import com.enderio.base.data.loot.EIOLootModifiersProvider;
import com.enderio.base.data.recipe.BlockRecipeProvider;
import com.enderio.base.data.recipe.FilterRecipeProvider;
import com.enderio.base.data.recipe.FireCraftingRecipeProvider;
import com.enderio.base.data.recipe.GlassRecipeProvider;
import com.enderio.base.data.recipe.ItemRecipeProvider;
import com.enderio.base.data.recipe.MaterialRecipeProvider;
import com.enderio.base.data.tags.EIOBlockTagsProvider;
import com.enderio.base.data.tags.EIOEntityTagsProvider;
import com.enderio.base.data.tags.EIOFluidTagsProvider;
import com.enderio.base.data.tags.EIOItemTagsProvider;
import com.enderio.regilite.Regilite;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
@Mod(EnderIOBase.MODULE_MOD_ID)
public class EnderIOBase {
    public static final String MODULE_MOD_ID = "enderio_base";

    public static final Regilite REGILITE = new Regilite(EnderIO.NAMESPACE);

    public static IEventBus modEventBus;
    public static ModContainer modContainer;

    public EnderIOBase(IEventBus modEventBus, ModContainer modContainer) {
        EnderIOBase.modEventBus = modEventBus;
        EnderIOBase.modContainer = modContainer;

        // Ensure the enderio config subdirectory is present.
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(EnderIO.NAMESPACE));
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
        REGILITE.register(modEventBus);

        // Run datagen after registrate is finished.
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);
        modEventBus.addListener(SoulVialItem::onCommonSetup);
        modEventBus.addListener(this::registerRegistries);
        Integrations.register();

        NeoForge.EVENT_BUS.addListener(PlayerMovementHandler::onPlayerTick);
    }

    private void registerRegistries(NewRegistryEvent event) {
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
        provider.addSubProvider(event.includeServer(), new FilterRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new EIOLootModifiersProvider(packOutput, lookupProvider));

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);
        provider.addSubProvider(event.includeServer(),
                new EIOItemTagsProvider(packOutput, lookupProvider, b.contentsGetter(), existingFileHelper));
        provider.addSubProvider(event.includeServer(),
                new EIOFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(),
                new EIOEntityTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(), new AdvancementProvider(packOutput, lookupProvider,
                existingFileHelper, List.of(new EIOAdvancementGenerator())));
        provider.addSubProvider(event.includeServer(),
                new LootTableProvider(packOutput, Collections.emptySet(), List
                        .of(new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST)),
                        lookupProvider));
        generator.addProvider(true, provider);
    }

    @SubscribeEvent
    public static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("inventorysorter", "slotblacklist", ItemFilterSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", FluidFilterSlot.class::getName);
    }
}
