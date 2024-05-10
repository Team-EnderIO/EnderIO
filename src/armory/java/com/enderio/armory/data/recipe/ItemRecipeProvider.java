package com.enderio.armory.data.recipe;

import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ItemRecipeProvider extends RecipeProvider {

    public ItemRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addDarkSteelTools(recipeOutput);
        addDarkSteelUpgrades(recipeOutput);
    }

    private void addDarkSteelTools(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, ArmoryItems.DARK_STEEL_SWORD.get())
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern(" I ")
            .pattern(" I ")
            .pattern(" S ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
            .save(recipeOutput);

//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.TOOLS, ArmoryItems.DARK_STEEL_PICKAXE.get())
//            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
//            .define('S', Tags.Items.RODS_WOODEN)
//            .pattern("III")
//            .pattern(" S ")
//            .pattern(" S ")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
//            .save(recipeOutput);
//
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.TOOLS, ArmoryItems.DARK_STEEL_AXE.get())
//            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
//            .define('S', Tags.Items.RODS_WOODEN)
//            .pattern("II")
//            .pattern("IS")
//            .pattern(" S")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT))
//            .save(recipeOutput);
    }

    private void addDarkSteelUpgrades(RecipeOutput recipeOutput) {
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_1, Ingredient.of(EIOItems.VIBRANT_CRYSTAL));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_2, Ingredient.of(EIOItems.BASIC_CAPACITOR));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_3, Ingredient.of(EIOItems.DOUBLE_LAYER_CAPACITOR));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EMPOWERED_4, Ingredient.of(EIOItems.OCTADIC_CAPACITOR));
//
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_FORK, Ingredient.of(Items.DIAMOND_HOE));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_SPOON, Ingredient.of(Items.DIAMOND_SHOVEL));
//
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.MISC, ArmoryItems.DARK_STEEL_UPGRADE_DIRECT.get())
//            .define('I', EIOItems.VIBRANT_ALLOY_INGOT.get())
//            .define('N', EIOItems.VIBRANT_ALLOY_NUGGET.get())
//            .define('E', Tags.Items.ENDER_PEARLS)
//            .define('B', EIOItems.DARK_STEEL_UPGRADE_BLANK.get())
//            .pattern("NIN")
//            .pattern("IEI")
//            .pattern("NBN")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()))
//            .save(recipeOutput);
//
//        // TODO: These are how they are in 1.12. When we redo dark steel upgrades this needs consideration again..
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_1, Ingredient.of(Items.TNT), Ingredient.of(EIOTags.Items.GEARS_WOOD));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_2, Ingredient.of(Items.TNT), Ingredient.of(EIOTags.Items.GEARS_STONE));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1, Ingredient.of(Items.CREEPER_HEAD));
//        addUpgrade(recipeOutput, ArmoryItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2, Ingredient.of(ItemTags.WOOL_CARPETS));
    }

    private void addUpgrade(RecipeOutput recipeOutput, ItemLike result, Ingredient... upgradeItems) {
//        var builder = ShapelessRecipeBuilder
//            .shapeless(RecipeCategory.MISC, result)
//            .requires(EIOItems.DARK_STEEL_UPGRADE_BLANK.get());
//
//        for (Ingredient i : upgradeItems) {
//            builder.requires(i);
//        }
//
//        builder.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ArmoryItems.DARK_STEEL_UPGRADE_BLANK.get()))
//            .save(recipeOutput);
    }
}
