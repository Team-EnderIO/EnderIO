package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.crafting.SizedFluidIngredient;

public class FermentingRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        build(new FluidStack(EIOFluids.HOOTCH.source().get(), 250), SizedFluidIngredient.of(FluidTags.WATER, 1000),
            EIOTags.Items.SEEDS, EIOTags.Items.CROPS, 200, recipeOutput);
        build(new FluidStack(EIOFluids.ROCKET_FUEL.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.source().get(), 1000), EIOTags.Items.EXPLOSIVES,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.NUTRIENT_DISTILLATION.source().get(), 250),
                SizedFluidIngredient.of(FluidTags.WATER, 1000), EIOTags.Items.MEAT, EIOTags.Items.CROPS, 200,
                recipeOutput);
        build(new FluidStack(EIOFluids.FIRE_WATER.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.source().get(), 1000), EIOTags.Items.BLAZE_POWDER,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_SUNSHINE.source().get(), 1000),
                SizedFluidIngredient.of(FluidTags.WATER, 1000), EIOTags.Items.NATURAL_LIGHTS,
            EIOTags.Items.SUNFLOWER, 200, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_DARKNESS.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.LIQUID_SUNSHINE.source().get(), 500), EIOTags.Items.AMETHYST,
                EIOTags.Items.DUSTS_OBSIDIAN, 600, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED.source().get(), 1000), SizedFluidIngredient.of(FluidTags.WATER, 1000),
            EIOTags.Items.PRISMARINE, EIOTags.Items.CLOUD_COLD, 400, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED_CONCENTRATED.source().get(), 500),
                SizedFluidIngredient.of(EIOFluids.CLOUD_SEED.source().get(), 1000),
            EIOTags.Items.LIGHTNING_ROD, EIOTags.Items.WIND_CHARGES, 600, recipeOutput);
    }

    protected void build(FluidStack output, SizedFluidIngredient input, TagKey<Item> firstReagent,
            TagKey<Item> secondReagent, int ticks, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("fermenting/" + BuiltInRegistries.FLUID.getKey(output.getFluid()).getPath()),
                new FermentingRecipe(input, firstReagent, secondReagent, output, ticks), null);
    }
}
