package com.enderio.base.data.recipe;

import com.enderio.base.common.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class FilterRecipeProvider extends RecipeProvider {

    public FilterRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_ITEM_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: forge:paper?
            .define('H', Items.HOPPER)
            .pattern(" P ")
            .pattern("PHP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.ADVANCED_ITEM_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: forge:paper?
            .define('Z', EIOItems.Z_LOGIC_CONTROLLER)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("RPR")
            .pattern("PZP")
            .pattern("RPR")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.Z_LOGIC_CONTROLLER))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_FLUID_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: forge:paper?
            .define('B', Items.BUCKET)
            .pattern(" P ")
            .pattern("PBP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BUCKET))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.ENTITY_FILTER.get())
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER)) // TODO: c:paper?
            .define('S', EIOItems.EMPTY_SOUL_VIAL)
            .pattern(" P ")
            .pattern("PSP")
            .pattern(" P ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.EMPTY_SOUL_VIAL))
            .save(consumer);
    }
}
