package com.enderio.base.data.recipe;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class ItemRecipeProvider extends RecipeProvider {

    public ItemRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addTools(recipeOutput);
        addGliders(recipeOutput);
        eraseFilterRecipes(recipeOutput);
    }

    private void addGliders(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER_WING.get())
                .pattern("  D")
                .pattern(" DL")
                .pattern("DLL")
                .define('D', EIOTags.Items.INGOTS_DARK_STEEL)
                .define('L', Tags.Items.LEATHERS)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.GLIDER.get())
                .pattern(" D ")
                .pattern("WDW")
                .define('D', EIOItems.DARK_STEEL_INGOT.get())
                .define('W', EIOItems.GLIDER_WING.get())
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER_WING.get()))
                .save(recipeOutput);

//        for (Map.Entry<DyeColor, ItemEntry<HangGliderItem>> dyeColorItemEntryEntry : EIOItems.COLORED_HANG_GLIDERS.entrySet()) {
//            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, dyeColorItemEntryEntry.getValue().get())
//                .requires(EIOItems.GLIDER.get())
//                .requires(dyeColorItemEntryEntry.getKey().getTag())
//                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GLIDER.get()))
//                .save(recipeOutput);
//        }
    }

    private void addTools(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.YETA_WRENCH.get())
                .define('I', Tags.Items.INGOTS_COPPER)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .pattern("I I")
                .pattern(" G ")
                .pattern(" I ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, EIOItems.COLD_FIRE_IGNITER.get())
                .requires(EIOTags.Items.INGOTS_DARK_STEEL)
                .requires(Items.FLINT)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.COORDINATE_SELECTOR.get())
                .define('I', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .define('C', Items.COMPASS)
                .define('E', Tags.Items.ENDER_PEARLS)
                .pattern("IEI")
                .pattern(" CI")
                .pattern("  I")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.COPPER_ALLOY_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.ELECTROMAGNET.get())
                .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .define('C', EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .define('E', EIOTags.Items.INGOTS_COPPER_ALLOY)
                .pattern("CVC")
                .pattern("C C")
                .pattern("E E")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.VOID_VIAL.get())
                .pattern(" S ")
                .pattern("QVQ")
                .pattern("GQG")
                .define('S', EIOTags.Items.INGOTS_SOULARIUM)
                .define('Q', EIOTags.Items.FUSED_QUARTZ)
                .define('V', EIOItems.SUSPICIOUS_SEED)
                .define('G', EIOItems.GRAINS_OF_INFINITY)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.LEVITATION_STAFF.get())
                .define('C', EIOTags.Items.GEMS_PRESCIENT_CRYSTAL)
                .define('R', EIOItems.INFINITY_ROD.get())
                .pattern("  C")
                .pattern(" R ")
                .pattern("R  ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.TRAVEL_STAFF.get())
                .define('C', EIOTags.Items.GEMS_ENDER_CRYSTAL)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("  C")
                .pattern(" I ")
                .pattern("I  ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ENDER_CRYSTAL.get()))
                .save(recipeOutput);
    }

    private void eraseFilterRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.BASIC_ITEM_FILTER)
                .requires(EIOItems.BASIC_ITEM_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.BASIC_ITEM_FILTER))
                .save(recipeOutput, EnderIO.loc("erase_basic_item_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.ADVANCED_ITEM_FILTER)
                .requires(EIOItems.ADVANCED_ITEM_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.ADVANCED_ITEM_FILTER))
                .save(recipeOutput, EnderIO.loc("erase_advanced_item_filter"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.BASIC_FLUID_FILTER)
                .requires(EIOItems.BASIC_FLUID_FILTER)
                .unlockedBy("has_ingredient", has(EIOItems.BASIC_FLUID_FILTER))
                .save(recipeOutput, EnderIO.loc("erase_basic_fluid_filter"));
    }
}
