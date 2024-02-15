package com.enderio.armory.common.init;

import com.enderio.EnderIO;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ArmoryRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnderIO.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, DarkSteelUpgradeRecipe.Serializer> DARK_STEEL_UPGRADE = RECIPE_SERIALIZERS.register("dark_steel_upgrade", DarkSteelUpgradeRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
