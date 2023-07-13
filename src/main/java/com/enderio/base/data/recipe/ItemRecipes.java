package com.enderio.base.data.recipe;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.misc.MaterialItem;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ItemRecipes extends RecipeProvider {

    public ItemRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        addTools(recipeConsumer);
        addDarkSteelTools(recipeConsumer);
        addDarkSteelUpgrades(recipeConsumer);
        addGliders(recipeConsumer);
    }

    private void addGliders(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER_WING.get())
            .pattern("  D")
            .pattern(" DL")
            .pattern("DLL")
            .define('D', EIOItems.DARK_STEEL_INGOT.get())
            .define('L', Tags.Items.LEATHER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

//        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER.get())
//            .pattern(" D ")
//            .pattern("WDW")
//            .define('D', EIOItems.DARK_STEEL_INGOT.get())
//            .define('W', EIOItems.GLIDER_WING.get())
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER_WING.get()))
//            .save(recipeConsumer);
//
//        for (Map.Entry<DyeColor, ItemEntry<HangGliderItem>> dyeColorItemEntryEntry : EIOItems.COLORED_HANG_GLIDERS.entrySet()) {
//            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, dyeColorItemEntryEntry.getValue().get())
//                .requires(EIOItems.GLIDER.get())
//                .requires(dyeColorItemEntryEntry.getKey().getTag())
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER.get()))
//                .save(recipeConsumer);
//        }
    }

    private void addTools(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.YETA_WRENCH.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('G', EIOItems.GEAR_STONE.get())
            .pattern("I I")
            .pattern(" G ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeConsumer);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.TOOLS, EIOItems.COLD_FIRE_IGNITER.get())
            .requires(EIOItems.DARK_STEEL_INGOT.get())
            .requires(Items.FLINT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.COORDINATE_SELECTOR.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('C', Items.COMPASS)
            .define('E', Tags.Items.ENDER_PEARLS)
            .pattern("IEI")
            .pattern(" CI")
            .pattern("  I")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.ELECTROMAGNET.get())
            .define('V', EIOItems.VIBRANT_CRYSTAL.get())
            .define('C', EIOItems.CONDUCTIVE_ALLOY_INGOT.get())
            .define('E', EIOItems.COPPER_ALLOY_INGOT.get())
            .pattern("CVC")
            .pattern("C C")
            .pattern("E E")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_CRYSTAL.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.EXPERIENCE_ROD.get())
            .pattern("  I")
            .pattern(" E ")
            .pattern("I  ")
            .define('I', EIOItems.SOULARIUM_INGOT)
            .define('E', EIOItems.ENERGETIC_ALLOY_INGOT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.LEVITATION_STAFF.get())
            .define('C', EIOItems.PRESCIENT_CRYSTAL.get())
            .define('R', EIOItems.INFINITY_ROD.get())
            .pattern("  C")
            .pattern(" R ")
            .pattern("R  ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeConsumer);
    }

    private void addDarkSteelTools(Consumer<FinishedRecipe> recipeConsumer) {
        MaterialItem ingot = EIOItems.DARK_STEEL_INGOT.get();

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.DARK_STEEL_SWORD.get())
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern(" I ")
            .pattern(" I ")
            .pattern(" S ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);

//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.TOOLS, EIOItems.DARK_STEEL_PICKAXE.get())
//            .define('I', ingot)
//            .define('S', Tags.Items.RODS_WOODEN)
//            .pattern("III")
//            .pattern(" S ")
//            .pattern(" S ")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
//            .save(recipeConsumer);
//
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.TOOLS, EIOItems.DARK_STEEL_AXE.get())
//            .define('I', ingot)
//            .define('S', Tags.Items.RODS_WOODEN)
//            .pattern("II")
//            .pattern("IS")
//            .pattern(" S")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
//            .save(recipeConsumer);
    }

    private void addDarkSteelUpgrades(Consumer<FinishedRecipe> recipeConsumer) {

//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_1, Ingredient.of(EIOItems.VIBRANT_CRYSTAL));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_2, Ingredient.of(EIOItems.BASIC_CAPACITOR));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_3, Ingredient.of(EIOItems.DOUBLE_LAYER_CAPACITOR));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_4, Ingredient.of(EIOItems.OCTADIC_CAPACITOR));
//
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_FORK, Ingredient.of(Items.DIAMOND_HOE));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_SPOON, Ingredient.of(Items.DIAMOND_SHOVEL));
//
//        ShapedRecipeBuilder
//            .shaped(RecipeCategory.MISC, EIOItems.DARK_STEEL_UPGRADE_DIRECT.get())
//            .define('I', EIOItems.VIBRANT_ALLOY_INGOT.get())
//            .define('N', EIOItems.VIBRANT_ALLOY_NUGGET.get())
//            .define('E', Tags.Items.ENDER_PEARLS)
//            .define('B', EIOItems.DARK_STEEL_UPGRADE_BLANK.get())
//            .pattern("NIN")
//            .pattern("IEI")
//            .pattern("NBN")
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()))
//            .save(recipeConsumer);
//
//        // TODO: These are how they are in 1.12. When we redo dark steel upgrades this needs consideration again..
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_1, Ingredient.of(Items.TNT), Ingredient.of(EIOTags.Items.GEARS_WOOD));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_2, Ingredient.of(Items.TNT), Ingredient.of(EIOTags.Items.GEARS_STONE));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_1, Ingredient.of(Items.CREEPER_HEAD));
//        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EXPLOSIVE_PENETRATION_2, Ingredient.of(ItemTags.WOOL_CARPETS));
    }

    private void addUpgrade(Consumer<FinishedRecipe> recipeConsumer, ItemLike result, Ingredient... upgradeItems) {
//        var builder = ShapelessRecipeBuilder
//            .shapeless(RecipeCategory.MISC, result)
//            .requires(EIOItems.DARK_STEEL_UPGRADE_BLANK.get());
//
//        for (Ingredient i : upgradeItems) {
//            builder.requires(i);
//        }
//
//        builder.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()))
//            .save(recipeConsumer);
    }
}
