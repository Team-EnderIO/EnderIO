package com.enderio.base.datagen.recipe.standard;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.recipe.GrindingballRecipe;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class GrindingballRecipeGenerator extends RecipeProvider {

    public GrindingballRecipeGenerator(DataGenerator pGenerator) {
        super(pGenerator);
    }
    
    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(Items.FLINT, 1.2F, 1.25F, 0.85F, 24000, pFinishedRecipeConsumer);
        build(EIOItems.DARK_STEEL_BALL.get(), 1.35F, 2.00F, 0.7F, 125000, pFinishedRecipeConsumer);
        build(EIOItems.COPPER_ALLOY_BALL.get(), 1.2F, 1.65F, 0.8F, 40000, pFinishedRecipeConsumer);
        build(EIOItems.ENERGETIC_ALLOY_BALL.get(), 1.6F, 1.1F, 1.1F, 80000, pFinishedRecipeConsumer);
        build(EIOItems.VIBRANT_ALLOY_BALL.get(), 1.75F, 1.35F, 1.13F, 80000, pFinishedRecipeConsumer);
        build(EIOItems.REDSTONE_ALLOY_BALL.get(), 1.00F, 1.00F, 0.35F, 30000, pFinishedRecipeConsumer);
        build(EIOItems.CONDUCTIVE_ALLOY_BALL.get(), 1.35F, 1.00F, 1.0F, 40000, pFinishedRecipeConsumer);
        build(EIOItems.PULSATING_ALLOY_BALL.get(), 1.00F, 1.85F, 1.0F, 100000, pFinishedRecipeConsumer);
        build(EIOItems.SOULARIUM_BALL.get(), 1.2F, 2.15F, 0.9F, 80000, pFinishedRecipeConsumer);
        build(EIOItems.END_STEEL_BALL.get(), 1.4F, 2.4F, 0.7F, 75000, pFinishedRecipeConsumer);
    }

    protected void build(Item item, float grinding, float chance, float power, int durability, Consumer<FinishedRecipe> recipeConsumer) {
        build(new GrindingballRecipe(null, Ingredient.of(item), grinding, chance, power, durability), item.getRegistryName().getPath(), recipeConsumer);
    }

    protected void build(GrindingballRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new FinishedGrindingballRecipe(recipe, name));
    }

}
