package com.enderio.core.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class EnderRecipeProvider extends RecipeProvider {

    private final List<SubRecipeProvider> subRecipeProviders = new ArrayList<>();

    public EnderRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        for (var provider : subRecipeProviders) {
            provider.buildRecipes(recipeOutput, holderLookup);
        }
    }

    protected void addProvider(SubRecipeProvider provider) {
        subRecipeProviders.add(provider);
    }
}
