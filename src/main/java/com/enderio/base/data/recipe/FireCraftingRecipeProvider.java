package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FireCraftingRecipeProvider extends EnderRecipeProvider {
    public FireCraftingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.loc("fire_crafting/infinity"),
            new FireCraftingRecipe(
                ResourceKey.create(Registries.LOOT_TABLE, EnderIO.loc("fire_crafting/infinity")),
                1,
                List.of(Blocks.BEDROCK),
                List.of(),
                List.of(Level.OVERWORLD)),
            null);
    }
}
