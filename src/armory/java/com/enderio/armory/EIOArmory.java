package com.enderio.armory;

import com.enderio.EnderIO;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.init.ArmoryLootModifiers;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.armory.data.loot.ArmoryLootModifiersProvider;
import com.enderio.armory.data.recipe.ItemRecipeProvider;
import com.enderio.armory.data.tags.ArmoryBlockTagsProvider;
import com.enderio.base.data.EIODataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOArmory {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Register config files
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, ArmoryConfig.COMMON_SPEC, "enderio/armory-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, ArmoryConfig.CLIENT_SPEC, "enderio/armory-client.toml");

        // Get event bus
        IEventBus modEventBus = EnderIO.modEventBus;

        // Perform initialization and registration for everything so things are registered.
        ArmoryItems.register(modEventBus);
        ArmoryRecipes.register(modEventBus);
        ArmoryLootModifiers.register(modEventBus);
        ArmoryTags.register();
        ArmoryLang.register();
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("armory");

        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new ArmoryLootModifiersProvider(packOutput));

        var b = new ArmoryBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);

        event.getGenerator().addProvider(true, provider);
    }
}
