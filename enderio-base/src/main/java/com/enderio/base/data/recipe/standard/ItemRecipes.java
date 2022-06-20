package com.enderio.base.data.recipe.standard;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.darksteel.DarkSteelUpgradeItem;
import com.enderio.base.common.item.misc.MaterialItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ItemRecipes extends RecipeProvider {

    public ItemRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        addTools(recipeConsumer);
        addDarkSteelTools(recipeConsumer);
        addDarkSteelUpgrades(recipeConsumer);
    }

    private void addTools(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(EIOItems.YETA_WRENCH.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('G', EIOItems.GEAR_STONE.get())
            .pattern("I I")
            .pattern(" G ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeConsumer);

        ShapelessRecipeBuilder
            .shapeless(EIOItems.COLD_FIRE_IGNITER.get())
            .requires(EIOItems.DARK_STEEL_INGOT.get())
            .requires(Items.FLINT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.COORDINATE_SELECTOR.get())
            .define('I', EIOItems.COPPER_ALLOY_INGOT.get())
            .define('C', Items.COMPASS)
            .define('E', Tags.Items.ENDER_PEARLS)
            .pattern("IEI")
            .pattern(" CI")
            .pattern("  I")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.ELECTROMAGNET.get())
            .define('V', EIOItems.VIBRANT_CRYSTAL.get())
            .define('C', EIOItems.CONDUCTIVE_ALLOY_INGOT.get())
            .define('E', EIOItems.COPPER_ALLOY_INGOT.get())
            .pattern("CVC")
            .pattern("C C")
            .pattern("E E")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_CRYSTAL.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.LEVITATION_STAFF.get())
            .define('C', EIOItems.PULSATING_CRYSTAL.get())
            .define('R', EIOItems.INFINITY_ROD.get())
            .pattern("  C")
            .pattern(" R ")
            .pattern("R  ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeConsumer);
    }

    private void addDarkSteelTools(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        MaterialItem ingot = EIOItems.DARK_STEEL_INGOT.get();

        ShapedRecipeBuilder
            .shaped(EIOItems.DARK_STEEL_PICKAXE.get())
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern("III")
            .pattern(" S ")
            .pattern(" S ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.DARK_STEEL_AXE.get())
            .define('I', ingot)
            .define('S', Tags.Items.RODS_WOODEN)
            .pattern("II")
            .pattern("IS")
            .pattern(" S")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);
    }

    private void addDarkSteelUpgrades(Consumer<FinishedRecipe> recipeConsumer) {

        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_1, EIOItems.VIBRANT_CRYSTAL);
        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_2, EIOItems.BASIC_CAPACITOR);
        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_3, EIOItems.DOUBLE_LAYER_CAPACITOR);
        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_EMPOWERED_4, EIOItems.OCTADIC_CAPACITOR);

        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_FORK, Items.DIAMOND_HOE);
        addUpgrade(recipeConsumer, EIOItems.DARK_STEEL_UPGRADE_SPOON, Items.DIAMOND_SHOVEL);

        ShapedRecipeBuilder
            .shaped(EIOItems.DARK_STEEL_UPGRADE_DIRECT.get())
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT.get())
            .define('N', EIOItems.VIBRANT_ALLOY_NUGGET.get())
            .define('E', Tags.Items.ENDER_PEARLS)
            .define('B', EIOItems.DARK_STEEL_UPGRADE_BLANK.get())
            .pattern("NIN")
            .pattern("IEI")
            .pattern("NBN")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()))
            .save(recipeConsumer);
    }

    private void addUpgrade(Consumer<FinishedRecipe> recipeConsumer, ItemEntry<DarkSteelUpgradeItem> result, ItemEntry<?> upgradeItem) {
        addUpgrade(recipeConsumer, result.get(), upgradeItem.get());
    }

    private void addUpgrade(Consumer<FinishedRecipe> recipeConsumer, ItemEntry<DarkSteelUpgradeItem> result, ItemLike upgradeItem) {
        addUpgrade(recipeConsumer, result.get(), upgradeItem);
    }

    private void addUpgrade(Consumer<FinishedRecipe> recipeConsumer, ItemLike result, ItemLike upgradeItem) {
        ShapelessRecipeBuilder
            .shapeless(result)
            .requires(EIOItems.DARK_STEEL_UPGRADE_BLANK.get())
            .requires(upgradeItem)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()))
            .save(recipeConsumer);
    }
}
