package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

public class FilterRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_ITEM_FILTER.get())
                .define('P', Items.PAPER)
                .define('H', Items.HOPPER)
                .pattern(" P ")
                .pattern("PHP")
                .pattern(" P ")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.ADVANCED_ITEM_FILTER.get())
                .define('P',Items.PAPER)
                .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .pattern("RPR")
                .pattern("PZP")
                .pattern("RPR")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.Z_LOGIC_CONTROLLER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BIG_ITEM_FILTER.get())
                .define('P', Items.PAPER)
                .define('S', EIOItems.SKELETAL_CONTRACTOR)
                .define('D', EIOTags.Items.DUSTS_OBSIDIAN)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SKELETAL_CONTRACTOR))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BIG_ADVANCED_ITEM_FILTER.get())
                .define('S', Items.SHULKER_SHELL)
                .define('F', EIOItems.ADVANCED_ITEM_FILTER)
                .pattern("S")
                .pattern("F")
                .pattern("S")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ADVANCED_ITEM_FILTER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_FLUID_FILTER.get())
                .define('P',Items.PAPER)
                .define('B', Items.BUCKET)
                .pattern(" P ")
                .pattern("PBP")
                .pattern(" P ")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BUCKET))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_SOUL_FILTER.get())
            .define('P',Items.PAPER)
            .define('S', EIOItems.SOUL_VIAL)
            .pattern(" P ")
            .pattern("PSP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOUL_VIAL))
            .save(recipeOutput);

    }
}
