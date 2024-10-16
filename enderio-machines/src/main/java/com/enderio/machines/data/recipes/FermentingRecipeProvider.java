package com.enderio.machines.data.recipes;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.machines.common.recipe.FermentingRecipe;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

public class FermentingRecipeProvider extends RecipeProvider {

    public FermentingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(new FluidStack(EIOFluids.HOOTCH.getSource(), 250), SizedFluidIngredient.of(FluidTags.WATER, 1000), Tags.Items.SEEDS, Tags.Items.CROPS, 200, recipeOutput);
        build(new FluidStack(EIOFluids.ROCKET_FUEL.getSource(), 1000), SizedFluidIngredient.of(EIOFluids.HOOTCH.getSource(), 1000), MachineTags.ItemTags.EXPLOSIVES, Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.NUTRIENT_DISTILLATION.getSource(), 250), SizedFluidIngredient.of(FluidTags.WATER, 1000), ItemTags.MEAT, Tags.Items.CROPS, 200, recipeOutput);
        build(new FluidStack(EIOFluids.FIRE_WATER.getSource(), 1000), SizedFluidIngredient.of(EIOFluids.HOOTCH.getSource(), 1000), MachineTags.ItemTags.BLAZE_POWDER, Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_SUNSHINE.getSource(), 1000), SizedFluidIngredient.of(FluidTags.WATER, 1000), MachineTags.ItemTags.NATURAL_LIGHTS, MachineTags.ItemTags.SUNFLOWER, 200, recipeOutput);
    }

    protected void build(FluidStack output, SizedFluidIngredient input, TagKey<Item> leftReagent, TagKey<Item> rightReagent,
        int ticks, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIOBase.loc("fermenting/" + BuiltInRegistries.FLUID.getKey(output.getFluid()).getPath()),
            new FermentingRecipe(input, leftReagent, rightReagent, output, ticks), null);
    }
}
