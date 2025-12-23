package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.EnderRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class EnderIORecipeProvider extends EnderRecipeProvider {
    public EnderIORecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);

        addProvider(new AlloyRecipeProvider());
        addProvider(new ArmoryRecipeProvider());
        addProvider(new ConduitRecipeProvider());
        addProvider(new EnchanterRecipeProvider());
        addProvider(new FermentingRecipeProvider());
        addProvider(new FilterRecipeProvider());
        addProvider(new FireCraftingRecipeProvider());
        addProvider(new GlassRecipeProvider());
        addProvider(new ItemRecipeProvider());
        addProvider(new MachineRecipeProvider());
        addProvider(new MaterialRecipeProvider());
        addProvider(new MiscBlockRecipeProvider());
        addProvider(new PaintingRecipeProvider());
        addProvider(new SagMillRecipeProvider());
        addProvider(new SlicingRecipeProvider());
        addProvider(new SoulBindingRecipeProvider());
        addProvider(new TankRecipeProvider());
        addProvider(new WeatherChangeRecipeProvider());
    }

    public static final class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
        {
            return new EnderIORecipeProvider(registries, output);
        }

        @Override
        public String getName()
        {
            return "Ender IO Recipe Generator";
        }
    }
}
