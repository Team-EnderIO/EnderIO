package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public class ArmoryRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        addSet(recipeOutput, EIOTags.Items.INGOTS_DARK_STEEL, EIOItems.DARK_STEEL_INGOT.get(),
            EIOItems.DARK_STEEL_SWORD.get(), EIOItems.DARK_STEEL_PICKAXE.get(), EIOItems.DARK_STEEL_AXE.get(),
            EIOItems.DARK_STEEL_HELMET.get(), EIOItems.DARK_STEEL_CHESTPLATE.get(),
            EIOItems.DARK_STEEL_LEGGINGS.get(), EIOItems.DARK_STEEL_BOOTS.get());

        addSet(recipeOutput, EIOTags.Items.INGOTS_END_STEEL, EIOItems.END_STEEL_INGOT.get(),
            EIOItems.END_STEEL_SWORD.get(), EIOItems.END_STEEL_PICKAXE.get(), EIOItems.END_STEEL_AXE.get(),
            EIOItems.END_STEEL_HELMET.get(), EIOItems.END_STEEL_CHESTPLATE.get(),
            EIOItems.END_STEEL_LEGGINGS.get(), EIOItems.END_STEEL_BOOTS.get());
    }

    private void addSet(RecipeOutput recipeOutput, TagKey<Item> ingot, ItemLike unlockItem, ItemLike sword,
        ItemLike pickaxe, ItemLike axe, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
        var unlock = InventoryChangeTrigger.TriggerInstance.hasItems(unlockItem);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword)
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern(" I ")
            .pattern(" I ")
            .pattern(" S ")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe)
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern("III")
            .pattern(" S ")
            .pattern(" S ")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe)
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern("II")
            .pattern("IS")
            .pattern(" S")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
            .define('I', ingot)
            .pattern("III")
            .pattern("I I")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
            .define('I', ingot)
            .pattern("I I")
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
            .define('I', ingot)
            .pattern("III")
            .pattern("I I")
            .pattern("I I")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
            .define('I', ingot)
            .pattern("I I")
            .pattern("I I")
            .unlockedBy("has_ingredient", unlock)
            .save(recipeOutput);
    }
}
