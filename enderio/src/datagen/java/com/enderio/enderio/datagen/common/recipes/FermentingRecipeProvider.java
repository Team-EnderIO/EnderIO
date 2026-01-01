package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FermentingRecipeProvider extends SubRecipeProvider {

    private HolderLookup.RegistryLookup<Fluid> fluids;

    protected SizedFluidIngredient sizedFromTag(TagKey<Fluid> tag, int count) {
        return new SizedFluidIngredient(FluidIngredient.of(this.fluids.getOrThrow(tag)), count);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        this.fluids = registries.lookupOrThrow(Registries.FLUID);

        build(new FluidStack(EIOFluids.HOOTCH.source().get(), 250), sizedFromTag(FluidTags.WATER, 1000),
            EIOTags.Items.SEEDS, EIOTags.Items.CROPS, 200, recipeOutput);
        build(new FluidStack(EIOFluids.ROCKET_FUEL.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.source().get(), 1000), EIOTags.Items.EXPLOSIVES,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.NUTRIENT_DISTILLATION.source().get(), 250),
                sizedFromTag(FluidTags.WATER, 1000), EIOTags.Items.MEAT, EIOTags.Items.CROPS, 200,
                recipeOutput);
        build(new FluidStack(EIOFluids.FIRE_WATER.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.HOOTCH.source().get(), 1000), EIOTags.Items.BLAZE_POWDER,
                Tags.Items.DUSTS_REDSTONE, 400, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_SUNSHINE.source().get(), 1000),
                sizedFromTag(FluidTags.WATER, 1000), EIOTags.Items.NATURAL_LIGHTS,
            EIOTags.Items.SUNFLOWER, 200, recipeOutput);
        build(new FluidStack(EIOFluids.LIQUID_DARKNESS.source().get(), 1000),
                SizedFluidIngredient.of(EIOFluids.LIQUID_SUNSHINE.source().get(), 500), EIOTags.Items.AMETHYST,
                EIOTags.Items.DUSTS_OBSIDIAN, 600, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED.source().get(), 1000), sizedFromTag(FluidTags.WATER, 1000),
            EIOTags.Items.PRISMARINE, EIOTags.Items.CLOUD_COLD, 400, recipeOutput);
        build(new FluidStack(EIOFluids.CLOUD_SEED_CONCENTRATED.source().get(), 500),
                SizedFluidIngredient.of(EIOFluids.CLOUD_SEED_CONCENTRATED.source().get(), 1000),
            EIOTags.Items.LIGHTNING_ROD, EIOTags.Items.WIND_CHARGES, 600, recipeOutput);
    }

    protected void build(FluidStack output, SizedFluidIngredient input, TagKey<Item> leftReagent,
            TagKey<Item> rightReagent, int ticks, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("fermenting/" + BuiltInRegistries.FLUID.getKey(output.getFluid()).getPath())),
                new FermentingRecipe(input, leftReagent, rightReagent, output, ticks), null);
    }
}
