package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.common.init.EIOFluids;
import com.enderio.enderio.common.tag.EIOTags;
import com.enderio.enderio.machines.common.blocks.vat.FermentingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FermentingRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        build(new FluidStack(EIOFluids.HOOTCH.getSource(), 250), SizedFluidIngredient.of(FluidTags.WATER, 1000),
            EIOTags.Items.SEEDS, EIOTags.Items.CROPS, 200, recipeOutput);
        build(new FluidStack(EIOFluids.ROCKET_FUEL.getSource(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.getSource(), 1000), EIOTags.Items.EXPLOSIVES,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.NUTRIENT_DISTILLATION.getSource(), 250),
                SizedFluidIngredient.of(FluidTags.WATER, 1000), EIOTags.Items.MEAT, EIOTags.Items.CROPS, 200,
                recipeOutput);
        build(new FluidStack(EIOFluids.FIRE_WATER.getSource(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.getSource(), 1000), EIOTags.Items.BLAZE_POWDER,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_SUNSHINE.getSource(), 1000),
                SizedFluidIngredient.of(FluidTags.WATER, 1000), EIOTags.Items.NATURAL_LIGHTS,
            EIOTags.Items.SUNFLOWER, 200, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_DARKNESS.getSource(), 1000),
                SizedFluidIngredient.of(EIOFluids.LIQUID_SUNSHINE.getSource(), 500), EIOTags.Items.AMETHYST,
                EIOTags.Items.DUSTS_OBSIDIAN, 600, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED.getSource(), 1000), SizedFluidIngredient.of(FluidTags.WATER, 1000),
            EIOTags.Items.PRISMARINE, EIOTags.Items.CLOUD_COLD, 400, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED_CONCENTRATED.getSource(), 500),
                SizedFluidIngredient.of(EIOFluids.CLOUD_SEED_CONCENTRATED.getSource(), 1000),
            EIOTags.Items.LIGHTNING_ROD, EIOTags.Items.WIND_CHARGES, 600, recipeOutput);
    }

    protected void build(FluidStack output, SizedFluidIngredient input, TagKey<Item> leftReagent,
            TagKey<Item> rightReagent, int ticks, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("fermenting/" + BuiltInRegistries.FLUID.getKey(output.getFluid()).getPath()),
                new FermentingRecipe(input, leftReagent, rightReagent, output, ticks), null);
    }
}
