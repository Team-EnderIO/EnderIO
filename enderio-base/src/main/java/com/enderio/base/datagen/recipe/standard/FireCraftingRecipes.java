package com.enderio.base.datagen.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.EnderFinishedRecipe;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;

public class FireCraftingRecipes extends RecipeProvider {
    public FireCraftingRecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new EnderFinishedRecipe<>(
            new FireCraftingRecipe(
                null,
                EnderIO.loc("fire_crafting/infinity"),
                List.of(Blocks.BEDROCK),
                List.of(),
                List.of(Level.OVERWORLD.location())
            ), EnderIO.loc("fire_crafting/infinity")));
    }
}
