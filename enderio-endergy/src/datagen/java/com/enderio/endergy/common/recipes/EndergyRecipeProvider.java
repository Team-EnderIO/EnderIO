package com.enderio.endergy.common.recipes;

import com.enderio.core.data.recipe.EnderRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class EndergyRecipeProvider extends EnderRecipeProvider {
    public EndergyRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);

        addProvider(new AlloyRecipeProvider());
        addProvider(new MaterialRecipeProvider());
        addProvider(new SlicingRecipeProvider());
    }
}
