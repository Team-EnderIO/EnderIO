package com.enderio.armory;

import com.enderio.EnderIO;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.init.ArmoryLootModifiers;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.armory.data.loot.ArmoryLootModifiersProvider;
import com.enderio.armory.data.recipe.ItemRecipeProvider;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.EIODataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOArmory {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // Register config files
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, ArmoryConfig.COMMON_SPEC, "enderio/armory-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, ArmoryConfig.CLIENT_SPEC, "enderio/armory-client.toml");

        // Perform initialization and registration for everything so things are registered.
        ArmoryItems.register();
        ArmoryRecipes.register();
        ArmoryLootModifiers.register();
        ArmoryTags.register();
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        EIODataProvider provider = new EIODataProvider("armory");

        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput));
        provider.addSubProvider(event.includeServer(), new ArmoryLootModifiersProvider(packOutput));

        event.getGenerator().addProvider(true, provider);
    }
}
