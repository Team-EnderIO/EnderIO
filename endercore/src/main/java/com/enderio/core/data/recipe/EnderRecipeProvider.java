package com.enderio.core.data.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public abstract class EnderRecipeProvider extends RecipeProvider {

    private final List<SubRecipeProvider> subRecipeProviders = new ArrayList<>();

    protected EnderRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        for (var provider : subRecipeProviders) {
            provider.buildRecipes(output);
        }
    }

    protected void addProvider(SubRecipeProvider provider) {
        subRecipeProviders.add(provider);
    }
}
