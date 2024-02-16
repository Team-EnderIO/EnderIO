package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class FireCraftingRecipeProvider extends EnderRecipeProvider {
    public FireCraftingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.loc("fire_crafting/infinity"),
            new FireCraftingRecipe(
                EnderIO.loc("fire_crafting/infinity"),
                1,
                List.of(Blocks.BEDROCK),
                List.of(),
                List.of(Level.OVERWORLD.location())),
            null);
    }
}
