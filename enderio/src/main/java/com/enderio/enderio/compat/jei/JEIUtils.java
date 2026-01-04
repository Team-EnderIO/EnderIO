package com.enderio.enderio.compat.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class JEIUtils {
    public static <T extends Recipe<?>> IRecipeType<RecipeHolder<T>> createRecipeType(String namespace, String path, Class<T> recipeClass) {
        //noinspection unchecked
        Class<? extends RecipeHolder<T>> holderClass = (Class<? extends RecipeHolder<T>>) (Object) RecipeHolder.class;
        return IRecipeType.create(namespace, path, holderClass);
    }
}
