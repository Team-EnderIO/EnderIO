package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.core.common.recipes.FluidIngredient;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

public class FermentingRecipeProvider extends EnderRecipeProvider {

    public FermentingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(EIOFluids.HOOTCH.getSource(), FluidIngredient.of(Fluids.WATER), 1000, Tags.Items.SEEDS, Tags.Items.CROPS, 0.25, 20, recipeOutput);
    }

    protected void build(Fluid output, FluidIngredient input, int inputFluidAmount, TagKey<Item> leftReagent, TagKey<Item> rightReagent, double outputModifier,
        int ticks, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.loc("fermenting/" + BuiltInRegistries.FLUID.getKey(output).getPath()),
            new FermentingRecipe(input, inputFluidAmount, leftReagent, rightReagent, output, outputModifier, ticks), null);
    }
}
