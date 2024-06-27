package com.enderio.conduits.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.init.ConduitItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RedstoneFilterRecipes extends RecipeProvider {
    public RedstoneFilterRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.NOT_FILTER)
            .define('T', Items.REDSTONE_TORCH)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("TBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.OR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern(" I ")
            .pattern(" B ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.AND_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('T', Items.REDSTONE_TORCH)
            .pattern(" T ")
            .pattern(" B ")
            .pattern(" T ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.XOR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('T', Items.REDSTONE_TORCH)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern(" T ")
            .pattern("IBI")
            .pattern(" T ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TLATCH_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('L', Items.LEVER)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("LBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.COUNT_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .pattern("I  ")
            .pattern("IBI")
            .pattern("I  ")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.SENSOR_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('C', Items.COMPARATOR)
            .pattern("CBI")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ConduitItems.TIMER_FILTER)
            .define('B', EIOItems.REDSTONE_FILTER_BASE)
            .define('I', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('C', Items.CLOCK)
            .pattern("IBC")
            .unlockedBy("has_ingredient", has(EIOItems.REDSTONE_FILTER_BASE))
            .save(recipeOutput);

        conversionRecipes(recipeOutput);
    }

    private void conversionRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.OR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.NOR_FILTER))
            .save(recipeOutput, EnderIO.loc("or_filter_from_nor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.OR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.OR_FILTER))
            .save(recipeOutput, EnderIO.loc("nor_filter_from_or_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.AND_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.NAND_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.NAND_FILTER))
            .save(recipeOutput, EnderIO.loc("and_filter_from_nand_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.NAND_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.AND_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.AND_FILTER))
            .save(recipeOutput, EnderIO.loc("nand_filter_from_and_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XNOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.XNOR_FILTER))
            .save(recipeOutput, EnderIO.loc("xor_filter_from_xnor_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConduitItems.XNOR_FILTER)
            .requires(Items.REDSTONE_TORCH)
            .requires(ConduitItems.XOR_FILTER)
            .unlockedBy("has_ingredient", has(ConduitItems.XOR_FILTER))
            .save(recipeOutput, EnderIO.loc("xnor_filter_from_xor_filter"));
    }
}
