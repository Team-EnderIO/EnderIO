package com.enderio.core.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

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
