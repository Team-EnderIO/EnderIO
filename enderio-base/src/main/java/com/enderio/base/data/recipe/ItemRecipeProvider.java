package com.enderio.base.data.recipe;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ItemRecipeProvider extends RecipeProvider {

    public ItemRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addTools(recipeOutput);
        addGliders(recipeOutput);
    }

    private void addGliders(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER_WING.get())
            .pattern("  D")
            .pattern(" DL")
            .pattern("DLL")
            .define('D', EIOTags.Items.INGOTS_DARK_STEEL)
            .define('L', Tags.Items.LEATHERS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeOutput);

//        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER.get())
//            .pattern(" D ")
//            .pattern("WDW")
//            .define('D', EIOItems.DARK_STEEL_INGOT.get())
//            .define('W', EIOItems.GLIDER_WING.get())
//            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER_WING.get()))
//            .save(recipeOutput);
//
//        for (Map.Entry<DyeColor, ItemEntry<HangGliderItem>> dyeColorItemEntryEntry : EIOItems.COLORED_HANG_GLIDERS.entrySet()) {
//            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, dyeColorItemEntryEntry.getValue().get())
//                .requires(EIOItems.GLIDER.get())
//                .requires(dyeColorItemEntryEntry.getKey().getTag())
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER.get()))
//                .save(recipeOutput);
//        }
    }

    private void addTools(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.YETA_WRENCH.get())
            .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('G', EIOItems.GEAR_STONE)
            .pattern("I I")
            .pattern(" G ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.TOOLS, EIOItems.COLD_FIRE_IGNITER.get())
            .requires(EIOTags.Items.INGOTS_DARK_STEEL)
            .requires(Items.FLINT)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.COORDINATE_SELECTOR.get())
            .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .define('C', Items.COMPASS)
            .define('E', Tags.Items.ENDER_PEARLS)
            .pattern("IEI")
            .pattern(" CI")
            .pattern("  I")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.ELECTROMAGNET.get())
            .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .define('E', EIOTags.Items.INGOTS_COPPER_ALLOY)
            .pattern("CVC")
            .pattern("C C")
            .pattern("E E")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_CRYSTAL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.EXPERIENCE_ROD.get())
            .pattern("  I")
            .pattern(" E ")
            .pattern("I  ")
            .define('I', EIOTags.Items.INGOTS_SOULARIUM)
            .define('E', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.LEVITATION_STAFF.get())
            .define('C', EIOTags.Items.GEMS_PRESCIENT_CRYSTAL)
            .define('R', EIOItems.INFINITY_ROD.get())
            .pattern("  C")
            .pattern(" R ")
            .pattern("R  ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, EIOItems.TRAVEL_STAFF.get())
            .define('C', EIOTags.Items.GEMS_ENDER_CRYSTAL)
            .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
            .pattern("  C")
            .pattern(" I ")
            .pattern("I  ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENDER_CRYSTAL.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.TOOLS, EIOItems.VOID_SEED.get())
            .requires(EIOItems.EMPTY_SOUL_VIAL)
            .requires(EIOItems.GRAINS_OF_INFINITY)
            .requires(Tags.Items.SEEDS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.EMPTY_SOUL_VIAL))
            .save(recipeOutput);
    }
}
