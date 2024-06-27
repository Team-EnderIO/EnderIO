package com.enderio.base.data.recipe;

import com.enderio.base.common.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class FilterRecipeProvider extends RecipeProvider {

    public FilterRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_ITEM_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: c:paper?
            .define('H', Items.HOPPER)
            .pattern(" P ")
            .pattern("PHP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.ADVANCED_ITEM_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: c:paper?
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("RPR")
            .pattern("PZP")
            .pattern("RPR")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.Z_LOGIC_CONTROLLER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_FLUID_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: c:paper?
            .define('B', Items.BUCKET)
            .pattern(" P ")
            .pattern("PBP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BUCKET))
            .save(recipeOutput);
    }
}
