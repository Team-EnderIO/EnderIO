package com.enderio.base.data.recipe;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FireCraftingRecipeProvider extends RecipeProvider {
    public FireCraftingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.loc("fire_crafting/bedrock_infinity"),
                new FireCraftingRecipe(List.of(
                        new FireCraftingRecipe.Result(EIOItems.GRAINS_OF_INFINITY.get().getDefaultInstance(), 1, 3,
                                0.8f),
                        new FireCraftingRecipe.Result(EIOItems.SUSPICIOUS_SEED.get().getDefaultInstance(), 1, 1, 0.3f)),
                        List.of(Blocks.BEDROCK), List.of(), List.of(Level.OVERWORLD), Optional.empty()),
                null);

        recipeOutput.accept(EnderIO.loc("fire_crafting/deepslate_infinity"),
                new FireCraftingRecipe(
                        List.of(new FireCraftingRecipe.Result(EIOItems.GRAINS_OF_INFINITY.get().getDefaultInstance(), 1,
                                1, 0.4f)),
                        List.of(Blocks.DEEPSLATE), List.of(), List.of(Level.OVERWORLD),
                        Optional.of(Blocks.COBBLESTONE)),
                null);
    }
}
