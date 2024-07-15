package com.enderio.base.data.recipe;

import com.enderio.EnderIOBase;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.data.loot.FireCraftingLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FireCraftingRecipeProvider extends RecipeProvider {
    public FireCraftingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIOBase.loc("fire_crafting/bedrock_infinity"),
            new FireCraftingRecipe(
                FireCraftingLootProvider.BEDROCK_CRAFTING,
                3,
                List.of(Blocks.BEDROCK),
                List.of(),
                List.of(Level.OVERWORLD)),
            null);

        recipeOutput.accept(EnderIOBase.loc("fire_crafting/deepslate_infinity"),
            new FireCraftingRecipe(
                FireCraftingLootProvider.DEEPSLATE_CRAFTING,
                1,
                List.of(Blocks.DEEPSLATE),
                List.of(),
                List.of(Level.OVERWORLD)),
            null);
    }
}
