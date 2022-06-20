package com.enderio.base.data.recipe.standard;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class StandardRecipes {

    public static void generate(boolean includeServer, DataGenerator dataGenerator) {
        dataGenerator.addProvider(includeServer, new MaterialRecipes(dataGenerator));
        dataGenerator.addProvider(includeServer, new BlockRecipes(dataGenerator));
        dataGenerator.addProvider(includeServer, new ItemRecipes(dataGenerator));
        dataGenerator.addProvider(includeServer, new GrindingBallRecipeProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new GlassRecipes(dataGenerator));
        dataGenerator.addProvider(includeServer, new FireCraftingRecipes(dataGenerator));
    }

    public static void saveRecipe(RecipeBuilder recipe, String variant, Consumer<FinishedRecipe> recipeConsumer) {
        ResourceLocation defaultLoc = ForgeRegistries.ITEMS.getKey(recipe.getResult());
        if(variant == null) {
            recipe.save(recipeConsumer);
        } else {
            recipe.save(recipeConsumer, new ResourceLocation(defaultLoc.getNamespace(), defaultLoc.getPath() + variant));
        }
    }
}
