package com.enderio;

import com.enderio.api.integration.IntegrationManager;
import com.enderio.base.common.advancement.PaintingTrigger;
import com.enderio.base.common.advancement.UseGliderTrigger;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.init.EIOEntities;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIOLootModifiers;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.init.EIOPackets;
import com.enderio.base.common.init.EIOParticles;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.EnderIOWrenchCompat;
import com.enderio.base.common.integrations.EnderIOSelfIntegration;
import com.enderio.base.common.item.tool.SoulVialItem;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.network.EIONetwork;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.EIODataProvider;
import com.enderio.base.data.advancement.EIOAdvancementGenerator;
import com.enderio.base.data.loot.ChestLootProvider;
import com.enderio.base.data.loot.EIOLootModifiersProvider;
import com.enderio.base.data.loot.FireCraftingLootProvider;
import com.enderio.base.data.recipe.BlockRecipeProvider;
import com.enderio.base.data.recipe.FireCraftingRecipeProvider;
import com.enderio.base.data.recipe.GlassRecipeProvider;
import com.enderio.base.data.recipe.GrindingBallRecipeProvider;
import com.enderio.base.data.recipe.ItemRecipeProvider;
import com.enderio.base.data.recipe.MaterialRecipeProvider;
import com.enderio.base.data.tags.EIOBlockTagsProvider;
import com.enderio.base.data.tags.EIOEntityTagsProvider;
import com.enderio.base.data.tags.EIOFluidTagsProvider;
import com.enderio.base.data.tags.EIOItemTagsProvider;
import com.enderio.core.EnderCore;
import com.enderio.core.common.network.CoreNetwork;
import com.tterrag.registrate.Registrate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.MissingMappingsEvent;
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

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }

    public EnderIO() {
        // Ensure the enderio config subdirectory is present.
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(MODID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register config files
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");

        // Setup core networking now
        CoreNetwork.networkInit();

        // Perform initialization and registration for everything so things are registered.
        EIOCreativeTabs.register();
        EIOItems.register();
        EIOBlocks.register();
        EIOBlockEntities.register();
        EIOFluids.register();
        EIOEnchantments.register();
        EIOTags.register();
        EIOMenus.register();
        EIOPackets.register();
        EIOLang.register();
        EIORecipes.register();
        EIOLootModifiers.register();
        EIOParticles.register();
        EIOEntities.register();

        MinecraftForge.EVENT_BUS.register(EnderIOWrenchCompat.class);

        // Run datagen after registrate is finished.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);
        modEventBus.addListener(SoulVialItem::onCommonSetup);
        IntegrationManager.addIntegration(EnderIOSelfIntegration.INSTANCE);
        new UseGliderTrigger().register();
        new PaintingTrigger().register();

        // Decor
        EIONetwork.register();

        // Remap
        MinecraftForge.EVENT_BUS.addListener(EnderIO::missingMappings);
    }

    public void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("base");

        provider.addSubProvider(event.includeServer(), new MaterialRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new BlockRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new GrindingBallRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new GlassRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new FireCraftingRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new EIOLootModifiersProvider(packOutput));

        var b = new EIOBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);
        provider.addSubProvider(event.includeServer(), new EIOItemTagsProvider(packOutput, lookupProvider, b.contentsGetter(), existingFileHelper));
        provider.addSubProvider(event.includeServer(), new EIOFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(), new EIOEntityTagsProvider(packOutput, lookupProvider, existingFileHelper));
        provider.addSubProvider(event.includeServer(),
            new ForgeAdvancementProvider(packOutput, lookupProvider, existingFileHelper, List.of(new EIOAdvancementGenerator())));
        provider.addSubProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
            List.of(new LootTableProvider.SubProviderEntry(FireCraftingLootProvider::new, LootContextParamSets.EMPTY),
                new LootTableProvider.SubProviderEntry(ChestLootProvider::new, LootContextParamSets.CHEST))));
        generator.addProvider(true, provider);
    }

    //TODO Remove later on when during beta/release.
    public static void missingMappings(MissingMappingsEvent event) {
        event.getMappings(Registries.ENCHANTMENT, EnderIO.MODID).forEach(mapping -> {
            if (mapping.getKey().equals(EnderIO.loc("withering_blade"))) {
                mapping.remap(EIOEnchantments.WITHERING.get());
            } else if (mapping.getKey().equals(EnderIO.loc("withering_arrow"))) {
                mapping.remap(EIOEnchantments.WITHERING.get());
            } else if (mapping.getKey().equals(EnderIO.loc("withering_bold"))) {
                mapping.remap(EIOEnchantments.WITHERING.get());
            }
        });
    }
}
