package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;

public class MaterialRecipeProvider extends RecipeProvider {
    public MaterialRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        addVanilla(recipeConsumer);
        addAlloys(recipeConsumer);
        addIngots(recipeConsumer);
        addCraftingComponents(recipeConsumer);
        addCapacitors(recipeConsumer);
        addCrystals(recipeConsumer);
        addGears(recipeConsumer);
        addGrindingBalls(recipeConsumer);

        // region Misc, to move

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.PHOTOVOLTAIC_COMPOSITE.get())
            .requires(EIOTags.Items.DUSTS_LAPIS)
            .requires(EIOTags.Items.DUSTS_COAL)
            .requires(EIOTags.Items.SILICON)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SILICON.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.INFINITY_ROD.get())
            .pattern(" NG")
            .pattern("NSN")
            .pattern("GN ")
            .define('N', EIOTags.Items.NUGGETS_DARK_STEEL)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .define('S', Items.STICK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EIOItems.EMPTY_SOUL_VIAL.get())
            .pattern(" S ")
            .pattern("Q Q")
            .pattern(" Q ")
            .define('S', EIOTags.Items.INGOTS_SOULARIUM)
            .define('Q', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
            .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EIOItems.BLACK_PAPER)
            .requires(Items.PAPER)
            .requires(Tags.Items.DYES_BLACK)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.REDSTONE_FILTER_BASE)
            .pattern("RPR")
            .pattern("PIP")
            .pattern("RPR")
            .define('R', EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .define('P', Ingredient.of(Items.PAPER, EIOItems.BLACK_PAPER))
            .define('I', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.REDSTONE_ALLOY_INGOT))
            .save(recipeConsumer);

        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.FOOD, EIOItems.ENDERIOS.get())
            .requires(Items.BOWL)
            .requires(Items.MILK_BUCKET)
            .requires(Items.WHEAT)
            .requires(EIOItems.POWDERED_ENDER_PEARL.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
            .save(recipeConsumer);

        // endregion
    }

    private void addVanilla(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.FOOD, Items.CAKE)
            .pattern("MMM")
            .pattern("SCS")
            .define('M', Items.MILK_BUCKET)
            .define('S', Items.SUGAR)
            .define('C', EIOItems.CAKE_BASE.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CAKE_BASE.get()))
            .save(recipeConsumer, EnderIO.loc("cake"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, Items.STICK, 16)
            .pattern("W")
            .pattern("W")
            .define('W', ItemTags.LOGS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.LOGS).build()))
            .save(recipeConsumer, EnderIO.loc("stick"));
    }

    private void addAlloys(Consumer<FinishedRecipe> recipeConsumer) {
        makeMaterialRecipes(recipeConsumer, EIOItems.COPPER_ALLOY_INGOT.get(), EIOItems.COPPER_ALLOY_NUGGET.get(), EIOBlocks.COPPER_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get(), EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get(), EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.REDSTONE_ALLOY_INGOT.get(), EIOItems.REDSTONE_ALLOY_NUGGET.get(), EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), EIOItems.CONDUCTIVE_ALLOY_NUGGET.get(),
            EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.PULSATING_ALLOY_INGOT.get(), EIOItems.PULSATING_ALLOY_NUGGET.get(), EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get(), EIOBlocks.DARK_STEEL_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.SOULARIUM_INGOT.get(), EIOItems.SOULARIUM_NUGGET.get(), EIOBlocks.SOULARIUM_BLOCK.get());
        makeMaterialRecipes(recipeConsumer, EIOItems.END_STEEL_INGOT.get(), EIOItems.END_STEEL_NUGGET.get(), EIOBlocks.END_STEEL_BLOCK.get());
    }

    private void addIngots(Consumer<FinishedRecipe> recipeConsumer) {
        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 200)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
            .save(recipeConsumer, EnderIO.loc(Items.IRON_INGOT + "_from_smelting"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(EIOItems.POWDERED_IRON.get()), RecipeCategory.MISC, Items.IRON_INGOT, 0, 100)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_IRON.get()))
            .save(recipeConsumer, EnderIO.loc(Items.IRON_INGOT + "_from_blasting"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 200)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
            .save(recipeConsumer, EnderIO.loc(Items.GOLD_INGOT + "_from_smelting"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(EIOItems.POWDERED_GOLD.get()), RecipeCategory.MISC, Items.GOLD_INGOT, 0, 100)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_GOLD.get()))
            .save(recipeConsumer, EnderIO.loc(Items.GOLD_INGOT + "_from_blasting"));

        SimpleCookingRecipeBuilder
            .smelting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT,  0, 200)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
            .save(recipeConsumer, EnderIO.loc(Items.COPPER_INGOT + "_from_smelting"));

        SimpleCookingRecipeBuilder
            .blasting(Ingredient.of(EIOItems.POWDERED_COPPER.get()), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 100)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.POWDERED_COPPER.get()))
            .save(recipeConsumer, EnderIO.loc(Items.COPPER_INGOT + "_from_blasting"));
    }

    private void addCraftingComponents(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.CONDUIT_BINDER_COMPOSITE.get(), 8)
            .pattern("GCG")
            .pattern("SGS")
            .pattern("GCG")
            .define('G', Tags.Items.GRAVEL)
            .define('S', Tags.Items.SAND)
            .define('C', Items.CLAY_BALL)
            .unlockedBy("has_ingredient_gravel", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GRAVEL))
            .unlockedBy("has_ingredient_sand", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SAND))
            .unlockedBy("has_ingredient_clay", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLAY_BALL))
            .save(recipeConsumer);

        MultipleCookingRecipeBuilder
            .smelting(Ingredient.of(EIOItems.CONDUIT_BINDER_COMPOSITE.get()), RecipeCategory.MISC, new ItemStack(EIOItems.CONDUIT_BINDER.get(), 2), 0, 200)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER_COMPOSITE.get()))
            .save(recipeConsumer, EnderIO.loc(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_smelting"));

        MultipleCookingRecipeBuilder
            .blasting(Ingredient.of(EIOItems.CONDUIT_BINDER_COMPOSITE.get()), RecipeCategory.MISC, new ItemStack(EIOItems.CONDUIT_BINDER.get(), 2), 0, 100)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CONDUIT_BINDER_COMPOSITE.get()))
            .save(recipeConsumer, EnderIO.loc(EIOItems.CONDUIT_BINDER.getId().getPath() + "_from_blasting"));
    }

    private void addCapacitors(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.BASIC_CAPACITOR.get())
            .pattern(" NG")
            .pattern("NIN")
            .pattern("GN ")
            .define('N', Tags.Items.NUGGETS_GOLD)
            .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .define('I', Tags.Items.INGOTS_COPPER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .pattern(" I ")
            .pattern("CDC")
            .pattern(" I ")
            .define('I', EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .define('C', EIOItems.BASIC_CAPACITOR.get())
            .define('D', EIOTags.Items.DUSTS_COAL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.BASIC_CAPACITOR.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.OCTADIC_CAPACITOR.get())
            .pattern(" I ")
            .pattern("CGC")
            .pattern(" I ")
            .define('I', EIOTags.Items.INGOTS_VIBRANT_ALLOY)
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('G', Items.GLOWSTONE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DOUBLE_LAYER_CAPACITOR.get()))
            .save(recipeConsumer);
    }

    private void addCrystals(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.PULSATING_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_ALLOY_NUGGET.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.VIBRANT_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOTags.Items.NUGGETS_VIBRANT_ALLOY)
            .define('D', Tags.Items.GEMS_EMERALD)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_ALLOY_NUGGET.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.WEATHER_CRYSTAL.get())
            .pattern(" P ")
            .pattern("VEV")
            .pattern(" P ")
            .define('P', EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .define('V', EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .define('E', EIOTags.Items.GEMS_ENDER_CRYSTAL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeConsumer);
    }

    private void addGears(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EIOItems.GEAR_WOOD.get())
            .pattern(" S ")
            .pattern("S S")
            .pattern(" S ")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.GEAR_WOOD.get())
            .pattern("S S")
            .pattern("   ")
            .pattern("S S")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_WOOD.getId().getPath() + "_corner"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.GEAR_STONE.get())
            .pattern("NIN")
            .pattern("I I")
            .pattern("NIN")
            .define('N', Tags.Items.RODS_WOODEN)
            .define('I', Tags.Items.COBBLESTONE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.COBBLESTONE).build()))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOItems.GEAR_STONE.get())
            .pattern(" I ")
            .pattern("IGI")
            .pattern(" I ")
            .define('I', Tags.Items.COBBLESTONE)
            .define('G', EIOTags.Items.GEARS_WOOD)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GEAR_WOOD.get()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_STONE.getId().getPath() + "_upgrade"));

        upgradeGear(recipeConsumer, EIOItems.GEAR_IRON.get(), EIOItems.GRAINS_OF_INFINITY.get(), Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
        upgradeGear(recipeConsumer, EIOItems.GEAR_ENERGIZED.get(), EIOItems.GEAR_IRON.get(), EIOItems.ENERGETIC_ALLOY_INGOT.get(),
            EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_VIBRANT.get(), EIOItems.GEAR_ENERGIZED.get(), EIOItems.VIBRANT_ALLOY_INGOT.get(),
            EIOItems.VIBRANT_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_DARK_STEEL.get(), EIOItems.GEAR_IRON.get(), EIOItems.DARK_STEEL_INGOT.get(),
            EIOItems.DARK_STEEL_NUGGET.get());
    }

    private void addGrindingBalls(Consumer<FinishedRecipe> recipeConsumer) {
        grindingBall(recipeConsumer, EIOItems.DARK_STEEL_BALL.get(), EIOTags.Items.INGOTS_DARK_STEEL, EIOItems.DARK_STEEL_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.SOULARIUM_BALL.get(), EIOTags.Items.INGOTS_SOULARIUM, EIOItems.SOULARIUM_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.CONDUCTIVE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY, EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.PULSATING_ALLOY_BALL.get(), EIOTags.Items.INGOTS_PULSATING_ALLOY, EIOItems.PULSATING_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.REDSTONE_ALLOY_BALL.get(), EIOTags.Items.INGOTS_REDSTONE_ALLOY, EIOItems.REDSTONE_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.ENERGETIC_ALLOY_BALL.get(), EIOTags.Items.INGOTS_ENERGETIC_ALLOY, EIOItems.ENERGETIC_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.VIBRANT_ALLOY_BALL.get(), EIOTags.Items.INGOTS_VIBRANT_ALLOY, EIOItems.VIBRANT_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.COPPER_ALLOY_BALL.get(), EIOTags.Items.INGOTS_COPPER_ALLOY, EIOItems.COPPER_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.END_STEEL_BALL.get(), EIOTags.Items.INGOTS_END_STEEL, EIOItems.END_STEEL_INGOT.get());
    }

    // region Helpers

	private void makeMaterialRecipes(Consumer<FinishedRecipe> recipeConsumer, Item ingot, Item nugget, Block block ) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
			.requires(block.asItem())
			.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
			.save(recipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
			.requires(ingot)
			.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
			.save(recipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
			.pattern("III")
	    	.pattern("III")
	    	.pattern("III")
	    	.define('I', ingot)
	    	.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
	    	.save(recipeConsumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
			.pattern("NNN")
    		.pattern("NNN")
    		.pattern("NNN")
    		.define('N', nugget)
    		.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
    		.save(recipeConsumer, EnderIO.loc(nugget.toString() + "_to_ingot"));
    }

    private void upgradeGear(Consumer<FinishedRecipe> recipeConsumer, Item resultGear, ItemLike inputGear, ItemLike cross, ItemLike corner) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultGear)
            .pattern("NIN")
            .pattern("IGI")
            .pattern("NIN")
            .define('N', corner)
            .define('I', cross)
            .define('G', inputGear)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
            .save(recipeConsumer);
    }

    private void upgradeGear(Consumer<FinishedRecipe> recipeConsumer, Item resultGear, ItemLike inputGear, TagKey<Item> cross, TagKey<Item> corner) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultGear)
            .pattern("NIN")
            .pattern("IGI")
            .pattern("NIN")
            .define('N', corner)
            .define('I', cross)
            .define('G', inputGear)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
            .save(recipeConsumer);
    }

    private void grindingBall(Consumer<FinishedRecipe> recipeConsumer, Item result, TagKey<Item> input, ItemLike trigger) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 24)
            .pattern(" I ")
            .pattern("III")
            .pattern(" I ")
            .define('I', input)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(trigger))
            .save(recipeConsumer);
    }

    // endregion

}
