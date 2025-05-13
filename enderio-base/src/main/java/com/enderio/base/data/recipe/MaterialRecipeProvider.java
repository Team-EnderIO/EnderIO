package com.enderio.base.data.recipe;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
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

public class MaterialRecipeProvider extends RecipeProvider {
    public MaterialRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addVanilla(recipeOutput);
        addAlloys(recipeOutput);
        addIngots(recipeOutput);
        addCraftingComponents(recipeOutput);
        addCapacitors(recipeOutput);
        addCrystals(recipeOutput);
        addGears(recipeOutput);
        addGrindingBalls(recipeOutput);

        // region Misc, to move

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.PHOTOVOLTAIC_COMPOSITE.get())
                .requires(EIOTags.Items.DUSTS_LAPIS)
                .requires(EIOTags.Items.DUSTS_COAL)
                .requires(EIOTags.Items.SILICON)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SILICON.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.INFINITY_ROD.get())
                .pattern(" NG")
                .pattern("NSN")
                .pattern("GN ")
                .define('N', EIOTags.Items.NUGGETS_DARK_STEEL)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('S', Items.STICK)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.SOUL_VIAL.get())
                .pattern(" S ")
                .pattern("Q Q")
                .pattern(" Q ")
                .define('S', EIOTags.Items.INGOTS_SOULARIUM)
                .define('Q', EIOTags.Items.FUSED_QUARTZ)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.BLACK_PAPER)
                .requires(Items.PAPER)
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.REDSTONE_FILTER_BASE)
                .pattern("RPR")
                .pattern("PIP")
                .pattern("RPR")
                .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER))
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_ALLOY_INGOT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, EIOItems.ENDERIOS.get())
                .requires(Items.BOWL)
                .requires(Items.MILK_BUCKET)
                .requires(Items.WHEAT)
                .requires(EIOItems.POWDERED_ENDER_PEARL.get())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
                .save(recipeOutput);

        // endregion
    }

    private void addVanilla(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.CAKE)
                .pattern("MMM")
                .pattern("SCS")
                .define('M', Items.MILK_BUCKET)
                .define('S', Items.SUGAR)
                .define('C', EIOItems.CAKE_BASE.get())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CAKE_BASE.get()))
                .save(recipeOutput, EnderIO.loc("cake"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STICK, 16)
                .pattern("W")
                .pattern("W")
                .define('W', ItemTags.LOGS)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance
                                .hasItems(ItemPredicate.Builder.item().of(ItemTags.LOGS).build()))
                .save(recipeOutput, EnderIO.loc("stick"));
    }

    private void addAlloys(RecipeOutput recipeOutput) {
        makeMaterialRecipes(recipeOutput, EIOItems.COPPER_ALLOY_INGOT.get(), EIOItems.COPPER_ALLOY_NUGGET.get(),
                EIOBlocks.COPPER_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get(),
                EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get(),
                EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.REDSTONE_ALLOY_INGOT.get(), EIOItems.REDSTONE_ALLOY_NUGGET.get(),
                EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), EIOItems.CONDUCTIVE_ALLOY_NUGGET.get(),
                EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.PULSATING_ALLOY_INGOT.get(), EIOItems.PULSATING_ALLOY_NUGGET.get(),
                EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get(),
                EIOBlocks.DARK_STEEL_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.SOULARIUM_INGOT.get(), EIOItems.SOULARIUM_NUGGET.get(),
                EIOBlocks.SOULARIUM_BLOCK.get());
        makeMaterialRecipes(recipeOutput, EIOItems.END_STEEL_INGOT.get(), EIOItems.END_STEEL_NUGGET.get(),
                EIOBlocks.END_STEEL_BLOCK.get());
    }

    private void addIngots(RecipeOutput recipeOutput) {
        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
                .save(recipeOutput, EnderIO.loc(Items.IRON_INGOT.getDescriptionId() + "_from_smelting"));

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
                .save(recipeOutput, EnderIO.loc(Items.IRON_INGOT.getDescriptionId() + "_from_blasting"));

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
                .save(recipeOutput, EnderIO.loc(Items.GOLD_INGOT.getDescriptionId() + "_from_smelting"));

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
                .save(recipeOutput, EnderIO.loc(Items.GOLD_INGOT.getDescriptionId() + "_from_blasting"));

        SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0,
                        200)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
                .save(recipeOutput, EnderIO.loc(Items.COPPER_INGOT.getDescriptionId() + "_from_smelting"));

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0,
                        100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
                .save(recipeOutput, EnderIO.loc(Items.COPPER_INGOT.getDescriptionId() + "_from_blasting"));
    }

    private void addCraftingComponents(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.CONDUIT_BINDER_COMPOSITE.get(), 8)
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
                .save(recipeOutput, EnderIO.loc(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_smelting"));

        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(EIOItems.CONDUIT_BINDER_COMPOSITE.get()), RecipeCategory.MISC,
                        new ItemStack(EIOItems.CONDUIT_BINDER.get(), 2), 0, 100)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER_COMPOSITE.get()))
                .save(recipeOutput, EnderIO.loc(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_blasting"));
    }

    private void addCapacitors(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.BASIC_CAPACITOR.get())
                .pattern(" NG")
                .pattern("NIN")
                .pattern("GN ")
                .define('N', Tags.Items.NUGGETS_GOLD)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('I', Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.DOUBLE_LAYER_CAPACITOR.get())
                .pattern(" I ")
                .pattern("CDC")
                .pattern(" I ")
                .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .define('C', EIOItems.BASIC_CAPACITOR.get())
                .define('D', EIOTags.Items.DUSTS_COAL)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.BASIC_CAPACITOR.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.OCTADIC_CAPACITOR.get())
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

    private void addCrystals(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.PULSATING_CRYSTAL.get())
                .pattern("PPP")
                .pattern("PDP")
                .pattern("PPP")
                .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_ALLOY_NUGGET.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.VIBRANT_CRYSTAL.get())
                .pattern("PPP")
                .pattern("PDP")
                .pattern("PPP")
                .define('P', EIOTags.Items.NUGGETS_VIBRANT_ALLOY)
                .define('D', Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_ALLOY_NUGGET.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.WEATHER_CRYSTAL.get())
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

    private void addGears(RecipeOutput recipeOutput) {
        upgradeGear(recipeOutput, EIOItems.GEAR_IRON.get(), EIOItems.GRAINS_OF_INFINITY.get(), Tags.Items.INGOTS_IRON,
                Tags.Items.NUGGETS_IRON);
        upgradeGear(recipeOutput, EIOItems.GEAR_ENERGIZED.get(), EIOTags.Items.GEARS_IRON,
                EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        upgradeGear(recipeOutput, EIOItems.GEAR_VIBRANT.get(), EIOTags.Items.GEARS_ENERGIZED,
                EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get());
        upgradeGear(recipeOutput, EIOItems.GEAR_DARK_STEEL.get(), EIOTags.Items.GEARS_IRON,
                EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get());
    }

    private void addGrindingBalls(RecipeOutput recipeOutput) {
        grindingBall(recipeOutput, EIOItems.DARK_STEEL_BALL.get(), EIOTags.Items.INGOTS_DARK_STEEL,
                EIOItems.DARK_STEEL_INGOT.get());
        grindingBall(recipeOutput, EIOItems.SOULARIUM_BALL.get(), EIOTags.Items.INGOTS_SOULARIUM,
                EIOItems.SOULARIUM_INGOT.get());
        grindingBall(recipeOutput, EIOItems.CONDUCTIVE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY,
                EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.PULSATING_ALLOY_BALL.get(), EIOTags.Items.INGOTS_PULSATING_ALLOY,
                EIOItems.PULSATING_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.REDSTONE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_REDSTONE_ALLOY,
                EIOItems.REDSTONE_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.ENERGETIC_ALLOY_BALL.get(), EIOTags.Items.INGOTS_ENERGETIC_ALLOY,
                EIOItems.ENERGETIC_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.VIBRANT_ALLOY_BALL.get(), EIOTags.Items.INGOTS_VIBRANT_ALLOY,
                EIOItems.VIBRANT_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.COPPER_ALLOY_BALL.get(), EIOTags.Items.INGOTS_COPPER_ALLOY,
                EIOItems.COPPER_ALLOY_INGOT.get());
        grindingBall(recipeOutput, EIOItems.END_STEEL_BALL.get(), EIOTags.Items.INGOTS_END_STEEL,
                EIOItems.END_STEEL_INGOT.get());
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
                .save(recipeOutput, EnderIO.loc(nugget.getDescriptionId() + "_to_ingot"));
    }

    private void upgradeGear(RecipeOutput recipeOutput, Item resultGear, ItemLike inputGear, ItemLike cross,
            ItemLike corner) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
                .save(recipeOutput);
    }

    private void upgradeGear(RecipeOutput recipeOutput, Item resultGear, TagKey<Item> inputGear, ItemLike cross,
            ItemLike corner) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", has(inputGear))
                .save(recipeOutput);
    }

    private void upgradeGear(RecipeOutput recipeOutput, Item resultGear, ItemLike inputGear, TagKey<Item> cross,
            TagKey<Item> corner) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultGear)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', corner)
                .define('I', cross)
                .define('G', inputGear)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
                .save(recipeOutput);
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
