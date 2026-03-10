package com.enderio.core.data.recipe;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class EnderRecipeProvider extends RecipeProvider {

    private final List<SubRecipeProvider> subRecipeProviders = new ArrayList<>();

    public EnderRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        for (var provider : subRecipeProviders) {
            provider.buildRecipes(consumer);
        }
    }

    protected void addProvider(SubRecipeProvider provider) {
        subRecipeProviders.add(provider);
    }
}
