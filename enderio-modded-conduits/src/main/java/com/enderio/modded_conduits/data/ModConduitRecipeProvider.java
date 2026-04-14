package com.enderio.modded_conduits.data;

import com.enderio.modded_conduits.common.ModdedConduits;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModConduitRecipeProvider extends RecipeProvider {
    public ModConduitRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ModdedConduits.executeOnLoadedModules(module -> module.buildRecipes(registries, output));
    }

    public static final class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
        {
            return new ModConduitRecipeProvider(registries, output);
        }

        @Override
        public String getName()
        {
            return "Ender IO Modded Conduits Recipe Generator";
        }
    }
}
