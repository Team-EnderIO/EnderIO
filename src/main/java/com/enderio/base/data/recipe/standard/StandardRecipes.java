package com.enderio.base.data.recipe.standard;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class StandardRecipes {

    /**
     * @deprecated Going to rework recipe registration.
     */
    @Deprecated
    public static void saveRecipe(RecipeBuilder recipe, String variant, Consumer<FinishedRecipe> recipeConsumer) {
        ResourceLocation defaultLoc = ForgeRegistries.ITEMS.getKey(recipe.getResult());
        if(variant == null) {
            recipe.save(recipeConsumer);
        } else {
            recipe.save(recipeConsumer, new ResourceLocation(defaultLoc.getNamespace(), defaultLoc.getPath() + variant));
        }
    }
}
