package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.init.EIOFluids;
import com.enderio.enderio.common.content.machines.obelisks.weather.WeatherChangeRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class WeatherChangeRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        build(EnderIO.rl("clear"), EIOFluids.LIQUID_SUNSHINE.getSource(), 1000, WeatherChangeRecipe.WeatherMode.CLEAR,
                recipeOutput);
        build(EnderIO.rl("rain"), EIOFluids.CLOUD_SEED.getSource(), 1000, WeatherChangeRecipe.WeatherMode.RAIN,
                recipeOutput);
        build(EnderIO.rl("lightning"), EIOFluids.CLOUD_SEED_CONCENTRATED.getSource(), 1000,
                WeatherChangeRecipe.WeatherMode.LIGHTNING, recipeOutput);

    }

    protected void build(ResourceLocation id, Fluid fluid, int ammount, WeatherChangeRecipe.WeatherMode mode,
            RecipeOutput recipeOutput) {
        build(id, new FluidStack(fluid, ammount), mode, recipeOutput);
    }

    protected void build(ResourceLocation id, FluidStack fluid, WeatherChangeRecipe.WeatherMode mode,
            RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new WeatherChangeRecipe(fluid, mode), null);
    }
}
