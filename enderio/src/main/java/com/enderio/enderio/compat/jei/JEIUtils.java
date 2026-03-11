package com.enderio.enderio.compat.jei;

import net.minecraft.world.item.crafting.Recipe;

public class JEIUtils {
    public static <T extends Recipe<?>> mezz.jei.api.recipe.RecipeType<T> createRecipeType(String namespace, String path, Class<T> recipeClass) {
        //noinspection unchecked
        Class<? extends T> holderClass = (Class<? extends T>) (Object) RecipeHolder.class;
        return mezz.jei.api.recipe.RecipeType.create(namespace, path, holderClass);
    }
}
