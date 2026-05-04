package com.enderio.enderio.api.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface EnderIORecipe<T extends RecipeInput> extends Recipe<T> {

    // Not used by all recipes
    @Override
    default ItemStack assemble(T input) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isSpecial() {
        // TODO: Work out the consequences of this.
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default String group() {
        return "";
    }
}
