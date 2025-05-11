package com.enderio.modconduits.data;

import com.enderio.modconduits.common.ModdedConduits;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public class ModConduitRecipeProvider extends RecipeProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;

    public ModConduitRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        HolderLookup.Provider lookupProvider = registries.resultNow();
        ModdedConduits.executeOnLoadedModules(module -> module.buildRecipes(lookupProvider, recipeOutput));
    }
}
