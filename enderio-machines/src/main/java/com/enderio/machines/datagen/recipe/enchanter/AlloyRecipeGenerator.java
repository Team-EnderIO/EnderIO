package com.enderio.machines.datagen.recipe.enchanter;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.datagen.recipe.RecipeResult;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class AlloyRecipeGenerator extends RecipeProvider {

    public AlloyRecipeGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        // TODO: Review all recipes and alloy compositions
        // TODO: Experience values need set properly, i just used a filler value off the top of my head
        build(new ItemStack(EIOItems.ELECTRICAL_STEEL_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.INGOTS_IRON), Ingredient.of(EIOTags.Items.DUSTS_COAL), Ingredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.DUSTS_REDSTONE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.DUSTS_GLOWSTONE)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.VIBRANT_ALLOY_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()), Ingredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.REDSTONE_ALLOY_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.DUSTS_REDSTONE), Ingredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.CONDUCTIVE_IRON_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.DUSTS_REDSTONE), Ingredient.of(Tags.Items.INGOTS_IRON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.PULSATING_IRON_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.INGOTS_IRON), Ingredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DARK_STEEL_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.INGOTS_IRON), Ingredient.of(EIOTags.Items.DUSTS_COAL), Ingredient.of(Tags.Items.OBSIDIAN)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.SOULARIUM_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL), Ingredient.of(Tags.Items.INGOTS_GOLD)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.END_STEEL_INGOT.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Tags.Items.END_STONES), Ingredient.of(EIOItems.DARK_STEEL_INGOT.get()), Ingredient.of(Tags.Items.OBSIDIAN)), 20000, 0.3f, pFinishedRecipeConsumer);
        // What the fuck is iron alloy lmao
    }

    protected void build(ItemStack result, NonNullList<Ingredient> ingredients, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new AlloySmeltingRecipe(null, ingredients, result, energy, experience), result.getItem().getRegistryName().getPath(), recipeConsumer);
    }

    protected void build(AlloySmeltingRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new RecipeResult(recipe, name));
    }
}
