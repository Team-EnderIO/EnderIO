package com.enderio.machines.data.recipes;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.machines.common.blocks.obelisks.weather.WeatherChangeRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class WeatherChangeRecipeProvider extends RecipeProvider {

    public WeatherChangeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(EnderIO.loc("clear"), EIOFluids.LIQUID_SUNSHINE.getSource(), 1000, WeatherChangeRecipe.WeatherMode.CLEAR,
                recipeOutput);
        build(EnderIO.loc("rain"), EIOFluids.CLOUD_SEED.getSource(), 1000, WeatherChangeRecipe.WeatherMode.RAIN,
                recipeOutput);
        build(EnderIO.loc("lightning"), EIOFluids.CLOUD_SEED_CONCENTRATED.getSource(), 1000,
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
