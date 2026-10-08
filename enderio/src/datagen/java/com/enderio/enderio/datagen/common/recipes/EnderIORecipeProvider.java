package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.EnderRecipeProvider;
import com.enderio.enderio.api.EnderIORegistries;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

public class EnderIORecipeProvider extends EnderRecipeProvider {

    protected EnderIORecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);

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

    public static MultiRegistryBootstrap create() {
        return new MultiRegistryBootstrap() {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                // Return the registries we are adding entries to.
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(MultiRegistryBootstrap.BootstrapGetter registries) {
                // Run the recipe provider.
                new EnderIORecipeProvider(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT)).buildRecipes();
            }
        };
    }
}
