package com.enderio.base.datagen.recipe.standard;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class StandardRecipes {

    public static void generate(boolean includeServer, DataGenerator generator) {
        generator.addProvider(includeServer, new MaterialRecipes(generator));
        generator.addProvider(includeServer, new BlockRecipes(generator));
        generator.addProvider(includeServer, new ItemRecipes(generator));
        generator.addProvider(includeServer, new GrindingballRecipeGenerator(generator));
        generator.addProvider(includeServer, new GlassRecipes(generator));
        generator.addProvider(includeServer, new FireCraftingRecipes(generator));
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
