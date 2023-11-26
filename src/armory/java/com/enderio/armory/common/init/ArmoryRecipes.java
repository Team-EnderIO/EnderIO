package com.enderio.armory.common.init;

import com.enderio.EnderIO;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ArmoryRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnderIO.MODID);

    public static final RegistryObject<DarkSteelUpgradeRecipe.Serializer> DARK_STEEL_UPGRADE = RECIPE_SERIALIZERS.register("dark_steel_upgrade", DarkSteelUpgradeRecipe.Serializer::new);

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPE_SERIALIZERS.register(bus);
    }
}
