package com.enderio.core.common.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

/**
 * Some sensible base overrides for Ender IO recipes.
 */
public interface EnderRecipe<C extends Container> extends Recipe<C> {
    // region Common values

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    // endregion
}
