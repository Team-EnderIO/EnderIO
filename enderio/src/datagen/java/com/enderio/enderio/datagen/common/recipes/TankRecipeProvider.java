package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class TankRecipeProvider extends SubRecipeProvider {

    protected SizedFluidIngredient sizedFromTag(HolderLookup.RegistryLookup<Fluid> fluids, TagKey<Fluid> tag, int count) {
        return new SizedFluidIngredient(FluidIngredient.of(fluids.getOrThrow(tag)), count);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var fluids = registries.lookupOrThrow(Registries.FLUID);

        buildEmptying(Ingredient.of(Items.EXPERIENCE_BOTTLE), Items.GLASS_BOTTLE,
                sizedFromTag(fluids, Tags.Fluids.EXPERIENCE, 250), recipeOutput);
        buildFilling(Ingredient.of(Items.GLASS_BOTTLE), Items.EXPERIENCE_BOTTLE,
            sizedFromTag(fluids, Tags.Fluids.EXPERIENCE, 250), recipeOutput);

        buildEmptying(Ingredient.of(Items.WET_SPONGE), Items.SPONGE, SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.SPONGE), Items.WET_SPONGE, SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.STICK), EIOItems.NUTRITIOUS_STICK,
                SizedFluidIngredient.of(EIOFluids.NUTRIENT_DISTILLATION.source().get(), 1000), recipeOutput);

        buildFilling(Ingredient.of(Items.WHITE_CONCRETE_POWDER), Items.WHITE_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.ORANGE_CONCRETE_POWDER), Items.ORANGE_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.MAGENTA_CONCRETE_POWDER), Items.MAGENTA_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), Items.LIGHT_BLUE_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.YELLOW_CONCRETE_POWDER), Items.YELLOW_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.LIME_CONCRETE_POWDER), Items.LIME_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.PINK_CONCRETE_POWDER), Items.PINK_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.GRAY_CONCRETE_POWDER), Items.GRAY_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), Items.LIGHT_GRAY_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.CYAN_CONCRETE_POWDER), Items.CYAN_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.PURPLE_CONCRETE_POWDER), Items.PURPLE_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.BLUE_CONCRETE_POWDER), Items.BLUE_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.BROWN_CONCRETE_POWDER), Items.BROWN_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.GREEN_CONCRETE_POWDER), Items.GREEN_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
        buildFilling(Ingredient.of(Items.RED_CONCRETE_POWDER), Items.RED_CONCRETE, SizedFluidIngredient.of(Fluids.WATER, 1000),
                recipeOutput);
        buildFilling(Ingredient.of(Items.BLACK_CONCRETE_POWDER), Items.BLACK_CONCRETE,
                SizedFluidIngredient.of(Fluids.WATER, 1000), recipeOutput);
    }

    protected void buildEmptying(Ingredient input, ItemLike output, SizedFluidIngredient fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("tank_empty/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
                new TankRecipe(input, new ItemStack(output), fluid, TankRecipe.Mode.EMPTY), null);
    }

    protected void buildFilling(Ingredient input, ItemLike output, SizedFluidIngredient fluid, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("tank_fill/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
                new TankRecipe(input, new ItemStack(output), fluid, TankRecipe.Mode.FILL), null);
    }

}
