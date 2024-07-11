package com.enderio.armory;

import com.enderio.EnderIOBase;
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
import com.enderio.regilite.Regilite;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@Mod(EnderIOArmory.MODULE_MOD_ID)
public class EnderIOArmory {

    public static final String MODULE_MOD_ID = "enderio_armory";
    public static final String REGISTRY_NAMESPACE = EnderIOBase.REGISTRY_NAMESPACE;

    public static Regilite REGILITE = new Regilite(REGISTRY_NAMESPACE);

    public EnderIOArmory(IEventBus modEventBus, ModContainer modContainer) {
        // Register config files
        modContainer.registerConfig(ModConfig.Type.COMMON, ArmoryConfig.COMMON_SPEC, "enderio/armory-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ArmoryConfig.CLIENT_SPEC, "enderio/armory-client.toml");

        // Perform initialization and registration for everything so things are registered.
        ArmoryItems.register(modEventBus);
        ArmoryRecipes.register(modEventBus);
        ArmoryLootModifiers.register(modEventBus);
        ArmoryTags.register();
        ArmoryLang.register();

        REGILITE.register(modEventBus);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("armory");

        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new ArmoryLootModifiersProvider(packOutput, lookupProvider));

        var b = new ArmoryBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);

        event.getGenerator().addProvider(true, provider);
    }
}
