package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.content.fire_crafting.FireCraftingRecipe;
import com.enderio.enderio.common.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class FireCraftingRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        recipeOutput.accept(EnderIO.rl("fire_crafting/bedrock_infinity"),
                new FireCraftingRecipe(List.of(
                        new FireCraftingRecipe.Result(EIOItems.GRAINS_OF_INFINITY.get().getDefaultInstance(), 1, 3,
                                0.8f),
                        new FireCraftingRecipe.Result(EIOItems.SUSPICIOUS_SEED.get().getDefaultInstance(), 1, 1, 0.3f)),
                        List.of(Blocks.BEDROCK), List.of(), List.of(Level.OVERWORLD), Optional.empty()),
                null);

        recipeOutput.accept(EnderIO.rl("fire_crafting/deepslate_infinity"),
                new FireCraftingRecipe(
                        List.of(new FireCraftingRecipe.Result(EIOItems.GRAINS_OF_INFINITY.get().getDefaultInstance(), 1,
                                1, 0.4f)),
                        List.of(Blocks.DEEPSLATE), List.of(), List.of(Level.OVERWORLD),
                        Optional.of(Blocks.COBBLESTONE)),
                null);
    }
}
