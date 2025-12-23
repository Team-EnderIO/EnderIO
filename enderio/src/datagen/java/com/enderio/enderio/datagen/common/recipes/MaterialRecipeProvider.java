package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class MaterialRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var items = registries.lookupOrThrow(Registries.ITEM);

        addVanilla(items, recipeOutput);
        addAlloys(items, recipeOutput);
        addIngots(items, recipeOutput);
        addCraftingComponents(items, recipeOutput);
        addCapacitors(items, recipeOutput);
        addCrystals(items, recipeOutput);
        addGears(items, recipeOutput);
        addGrindingBalls(items, recipeOutput);

        // region Misc, to move

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, EIOItems.PHOTOVOLTAIC_COMPOSITE.get())
                .requires(EIOTags.Items.DUSTS_LAPIS)
                .requires(EIOTags.Items.DUSTS_COAL)
                .requires(EIOTags.Items.SILICON)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SILICON.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.INFINITY_ROD.get())
                .pattern(" NG")
                .pattern("NSN")
                .pattern("GN ")
                .define('N', EIOTags.Items.NUGGETS_DARK_STEEL)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, EIOItems.SOUL_VIAL.get())
                .pattern(" S ")
                .pattern("Q Q")
                .pattern(" Q ")
                .define('S', EIOTags.Items.INGOTS_SOULARIUM)
                .define('Q', EIOTags.Items.FUSED_QUARTZ)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, EIOItems.BLACK_PAPER)
                .requires(Items.PAPER)
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.REDSTONE_FILTER_BASE)
                .pattern("RPR")
                .pattern("PIP")
                .pattern("RPR")
                .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER))
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_ALLOY_INGOT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.FOOD, EIOItems.ENDERIOS.get())
                .requires(Items.BOWL)
                .requires(Items.MILK_BUCKET)
                .requires(Items.WHEAT)
                .requires(EIOItems.POWDERED_ENDER_PEARL.get())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
                .save(recipeOutput);

        // endregion
    }

    private void addVanilla(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.FOOD, Items.CAKE)
                .pattern("MMM")
                .pattern("SCS")
                .define('M', Items.MILK_BUCKET)
                .define('S', Items.SUGAR)
                .define('C', EIOItems.CAKE_BASE.get())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CAKE_BASE.get()))
                .save(recipeOutput, EnderIO.rl("cake").toString());

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.STICK, 16)
                .pattern("W")
                .pattern("W")
                .define('W', ItemTags.LOGS)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(items, ItemTags.LOGS).build()))
                .save(recipeOutput, EnderIO.rl("stick").toString());
    }

    private void addAlloys(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        makeMaterialRecipes(items, recipeOutput, EIOItems.COPPER_ALLOY_INGOT.get(), EIOItems.COPPER_ALLOY_NUGGET.get(),
                EIOBlocks.COPPER_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get(),
                EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get(),
                EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.REDSTONE_ALLOY_INGOT.get(), EIOItems.REDSTONE_ALLOY_NUGGET.get(),
                EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), EIOItems.CONDUCTIVE_ALLOY_NUGGET.get(),
                EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.PULSATING_ALLOY_INGOT.get(), EIOItems.PULSATING_ALLOY_NUGGET.get(),
                EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get(),
                EIOBlocks.DARK_STEEL_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.SOULARIUM_INGOT.get(), EIOItems.SOULARIUM_NUGGET.get(),
                EIOBlocks.SOULARIUM_BLOCK.get());
        makeMaterialRecipes(items, recipeOutput, EIOItems.END_STEEL_INGOT.get(), EIOItems.END_STEEL_NUGGET.get(),
                EIOBlocks.END_STEEL_BLOCK.get());
    }

    private void addIngots(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
                .save(recipeOutput, EnderIO.rl(Items.IRON_INGOT.getDescriptionId() + "_from_smelting").toString());

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
                .save(recipeOutput, EnderIO.rl(Items.IRON_INGOT.getDescriptionId() + "_from_blasting").toString());

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
                .save(recipeOutput, EnderIO.rl(Items.GOLD_INGOT.getDescriptionId() + "_from_smelting").toString());

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
                .save(recipeOutput, EnderIO.rl(Items.GOLD_INGOT.getDescriptionId() + "_from_blasting").toString());

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0,
                        200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
                .save(recipeOutput, EnderIO.rl(Items.COPPER_INGOT.getDescriptionId() + "_from_smelting").toString());

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0,
                        100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
                .save(recipeOutput, EnderIO.rl(Items.COPPER_INGOT.getDescriptionId() + "_from_blasting").toString());
    }

    private void addCraftingComponents(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.CONDUIT_BINDER_COMPOSITE.get(), 8)
                .pattern("GCG")
                .pattern("SGS")
                .pattern("GCG")
                .define('G', Tags.Items.GRAVELS)
                .define('S', Tags.Items.SANDS)
                .define('C', Items.CLAY_BALL)
                .unlockedBy("has_ingredient_gravel", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GRAVEL))
                .unlockedBy("has_ingredient_sand", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SAND))
                .unlockedBy("has_ingredient_clay", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLAY_BALL))
                .save(recipeOutput);

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.CONDUIT_BINDER_COMPOSITE.get()), RecipeCategory.MISC,
                        new ItemStack(EIOItems.CONDUIT_BINDER.get(), 2), 0, 200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER_COMPOSITE.get()))
                .save(recipeOutput, EnderIO.rl(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_smelting").toString());

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.CONDUIT_BINDER_COMPOSITE.get()), RecipeCategory.MISC,
                        new ItemStack(EIOItems.CONDUIT_BINDER.get(), 2), 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER_COMPOSITE.get()))
                .save(recipeOutput, EnderIO.rl(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_blasting").toString());
    }

    private void addCapacitors(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.BASIC_CAPACITOR.get())
                .pattern(" NG")
                .pattern("NIN")
                .pattern("GN ")
                .define('N', Tags.Items.NUGGETS_GOLD)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('I', Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CDC")
                .pattern(" I ")
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', EIOItems.BASIC_CAPACITOR.get())
                .define('D', EIOTags.Items.DUSTS_COAL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.BASIC_CAPACITOR.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.OCTADIC_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CGC")
                .pattern(" I ")
                .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
                .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .define('G', Items.GLOWSTONE)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DOUBLE_LAYER_CAPACITOR.get()))
                .save(recipeOutput);
    }

    private void addCrystals(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.PULSATING_CRYSTAL.get())
                .pattern("PPP")
                .pattern("PDP")
                .pattern("PPP")
                .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_ALLOY_NUGGET.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.VIBRANT_CRYSTAL.get())
                .pattern("PPP")
                .pattern("PDP")
                .pattern("PPP")
                .define('P', EIOTags.Items.NUGGETS_VIBRANT_ALLOY)
                .define('D', Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_ALLOY_NUGGET.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EIOItems.WEATHER_CRYSTAL.get())
                .pattern(" P ")
                .pattern("VEV")
                .pattern(" P ")
                .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .define('E', EIOTags.Items.GEMS_ENDER_CRYSTAL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
                .save(recipeOutput);
    }

    private void addGears(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        upgradeGear(items, recipeOutput, EIOItems.GEAR_IRON.get(), EIOItems.GRAINS_OF_INFINITY.get(), Tags.Items.INGOTS_IRON,
                Tags.Items.NUGGETS_IRON);
        upgradeGear(items, recipeOutput, EIOItems.GEAR_ENERGIZED.get(), EIOTags.Items.GEARS_IRON,
                EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        upgradeGear(items, recipeOutput, EIOItems.GEAR_VIBRANT.get(), EIOTags.Items.GEARS_ENERGIZED,
                EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get());
        upgradeGear(items, recipeOutput, EIOItems.GEAR_DARK_STEEL.get(), EIOTags.Items.GEARS_IRON,
                EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get());
    }

    private void addGrindingBalls(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        grindingBall(items, recipeOutput, EIOItems.DARK_STEEL_BALL.get(), EIOTags.Items.INGOTS_DARK_STEEL,
                EIOItems.DARK_STEEL_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.SOULARIUM_BALL.get(), EIOTags.Items.INGOTS_SOULARIUM,
                EIOItems.SOULARIUM_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.CONDUCTIVE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY,
                EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.PULSATING_ALLOY_BALL.get(), EIOTags.Items.INGOTS_PULSATING_ALLOY,
                EIOItems.PULSATING_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.REDSTONE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_REDSTONE_ALLOY,
                EIOItems.REDSTONE_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.ENERGETIC_ALLOY_BALL.get(), EIOTags.Items.INGOTS_ENERGETIC_ALLOY,
                EIOItems.ENERGETIC_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.VIBRANT_ALLOY_BALL.get(), EIOTags.Items.INGOTS_VIBRANT_ALLOY,
                EIOItems.VIBRANT_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.COPPER_ALLOY_BALL.get(), EIOTags.Items.INGOTS_COPPER_ALLOY,
                EIOItems.COPPER_ALLOY_INGOT.get());
        grindingBall(items, recipeOutput, EIOItems.END_STEEL_BALL.get(), EIOTags.Items.INGOTS_END_STEEL,
                EIOItems.END_STEEL_INGOT.get());
    }

    // region Helpers

    private void makeMaterialRecipes(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput, Item ingot, Item nugget, Block block) {
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ingot, 9)
                .requires(block.asItem())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, nugget, 9)
                .requires(ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, block)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ingot)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ingot)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', nugget)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                .save(recipeOutput, EnderIO.rl(nugget.getDescriptionId() + "_to_ingot").toString());
    }

    private void upgradeGear(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput, Item resultGear, ItemLike inputGear, ItemLike cross,
            ItemLike corner) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
                .save(recipeOutput);
    }

    private void upgradeGear(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput, Item resultGear, TagKey<Item> inputGear, ItemLike cross,
            ItemLike corner) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", has(items, inputGear))
                .save(recipeOutput);
    }

    private void upgradeGear(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput, Item resultGear, ItemLike inputGear, TagKey<Item> cross,
            TagKey<Item> corner) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
                .save(recipeOutput);
    }

    private void grindingBall(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput, Item result, TagKey<Item> input, ItemLike trigger) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, result, 24)
                .pattern(" I ")
                .pattern("III")
                .pattern(" I ")
                .define('I', input)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(trigger))
                .save(recipeOutput);
    }

    // endregion

}
