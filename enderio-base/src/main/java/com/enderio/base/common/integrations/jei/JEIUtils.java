package com.enderio.base.common.integrations.jei;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class JEIUtils {
    public static <T extends Recipe<?>> mezz.jei.api.recipe.RecipeType<RecipeHolder<T>> createRecipeType(String namespace, String path, Class<T> recipeClass) {
        //noinspection unchecked
        Class<? extends RecipeHolder<T>> holderClass = (Class<? extends RecipeHolder<T>>) (Object) RecipeHolder.class;
        return mezz.jei.api.recipe.RecipeType.create(namespace, path, holderClass);
    }
}
