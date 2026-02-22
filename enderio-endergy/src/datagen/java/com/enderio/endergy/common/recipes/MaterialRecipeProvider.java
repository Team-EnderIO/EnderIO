package com.enderio.endergy.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.endergy.common.init.EndergyBlocks;
import com.enderio.endergy.common.init.EndergyItems;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class MaterialRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        addAlloys(recipeOutput);
        addCapacitors(recipeOutput);
        addGrindingBalls(recipeOutput);
    }

    private void addAlloys(RecipeOutput recipeOutput) {
        makeMaterialRecipes(recipeOutput, EndergyItems.CRUDE_STEEL_INGOT.get(), EndergyItems.CRUDE_STEEL_NUGGET.get(),
                EndergyBlocks.CRUDE_STEEL_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EndergyItems.CRYSTALLINE_ALLOY_INGOT.get(), EndergyItems.CRYSTALLINE_ALLOY_NUGGET.get(),
                EndergyBlocks.CRYSTALLINE_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EndergyItems.MELODIC_ALLOY_INGOT.get(), EndergyItems.MELODIC_ALLOY_NUGGET.get(),
                EndergyBlocks.MELODIC_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EndergyItems.STELLAR_ALLOY_INGOT.get(), EndergyItems.STELLAR_ALLOY_NUGGET.get(),
                EndergyBlocks.STELLAR_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EndergyItems.VIVID_ALLOY_INGOT.get(), EndergyItems.VIVID_ALLOY_NUGGET.get(),
                EndergyBlocks.VIVID_ALLOY_BLOCK.get());
    }

    private void addCapacitors(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EndergyItems.GRAINY_CAPACITOR.get())
                .pattern("G")
                .pattern("N")
                .pattern("N")
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('N', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EndergyItems.VIVID_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CGC")
                .pattern(" I ")
                .define('I', EndergyItems.VIVID_ALLOY_INGOT)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR)
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EndergyItems.VIVID_ALLOY_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EndergyItems.CRYSTALLINE_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CPC")
                .pattern(" I ")
                .define('I', EndergyItems.CRYSTALLINE_ALLOY_INGOT)
                .define('C', Ingredient.of(EndergyItems.VIVID_CAPACITOR))
                .define('P', Tags.Items.GEMS_PRISMARINE)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EndergyItems.CRYSTALLINE_ALLOY_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EndergyItems.MELODIC_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CEC")
                .pattern(" I ")
                .define('I', EndergyItems.MELODIC_ALLOY_INGOT)
                .define('C', Ingredient.of(EndergyItems.CRYSTALLINE_CAPACITOR))
                .define('E', EIOTags.Items.INGOTS_END_STEEL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EndergyItems.MELODIC_ALLOY_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EndergyItems.STELLAR_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CSC")
                .pattern(" I ")
                .define('I', EndergyItems.STELLAR_ALLOY_INGOT)
                .define('C', Ingredient.of(EndergyItems.MELODIC_CAPACITOR))
                .define('S', Items.SHULKER_SHELL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EndergyItems.STELLAR_ALLOY_INGOT.get()))
                .save(recipeOutput);
    }

    private void addGrindingBalls(RecipeOutput recipeOutput) {
//        grindingBall(recipeOutput, EIOItems.DARK_STEEL_BALL.get(), EIOTags.Items.INGOTS_DARK_STEEL,
//                EIOItems.DARK_STEEL_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.SOULARIUM_BALL.get(), EIOTags.Items.INGOTS_SOULARIUM,
//                EIOItems.SOULARIUM_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.CONDUCTIVE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY,
//                EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.PULSATING_ALLOY_BALL.get(), EIOTags.Items.INGOTS_PULSATING_ALLOY,
//                EIOItems.PULSATING_ALLOY_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.REDSTONE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_REDSTONE_ALLOY,
//                EIOItems.REDSTONE_ALLOY_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.ENERGETIC_ALLOY_BALL.get(), EIOTags.Items.INGOTS_ENERGETIC_ALLOY,
//                EIOItems.ENERGETIC_ALLOY_INGOT.get());
//        grindingBall(recipeOutput, EIOItems.VIBRANT_ALLOY_BALL.get(), EIOTags.Items.INGOTS_VIBRANT_ALLOY,
//                EIOItems.VIBRANT_ALLOY_INGOT.get());
    }

    // region Helpers

    private void makeMaterialRecipes(RecipeOutput recipeOutput, Item ingot, Item nugget, Block block) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                .requires(block.asItem())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                .requires(ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', nugget)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput, EnderIO.rl(nugget.getDescriptionId() + "_to_ingot"));
    }

    private void grindingBall(RecipeOutput recipeOutput, Item result, TagKey<Item> input, ItemLike trigger) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 24)
                .pattern(" I ")
                .pattern("III")
                .pattern(" I ")
                .define('I', input)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(trigger))
                .save(recipeOutput);
    }

    // endregion

}
