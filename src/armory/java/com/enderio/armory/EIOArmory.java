package com.enderio.armory;

import com.enderio.EnderIO;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.init.ArmoryLootModifiers;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.armory.data.loot.ArmoryLootModifiersProvider;
import com.enderio.armory.data.recipe.ItemRecipeProvider;
import com.enderio.base.data.EIODataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIOArmory {
    @SubscribeEvent
    public static void onConstruct(FMLConstructModEvent event) {
        // TODO: Armory config

        // Perform initialization and registration for everything so things are registered.
        ArmoryItems.register();
        ArmoryRecipes.register();
        ArmoryLootModifiers.register();
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
