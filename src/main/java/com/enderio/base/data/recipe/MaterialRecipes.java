package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class MaterialRecipes extends RecipeProvider {
    public MaterialRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(Items.CAKE)
            .pattern("MMM")
            .pattern("SCS")
            .define('M', Items.MILK_BUCKET)
            .define('S', Items.SUGAR)
            .define('C', EIOItems.CAKE_BASE.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CAKE_BASE.get()))
            .save(recipeConsumer, EnderIO.loc("cake"));

        ShapelessRecipeBuilder
            .shapeless(EIOItems.PHOTOVOLTAIC_COMPOSITE.get())
            .requires(EIOTags.Items.DUSTS_LAPIS)
            .requires(EIOTags.Items.DUSTS_COAL)
            .requires(EIOTags.Items.SILICON)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SILICON.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.EMPTY_SOUL_VIAL.get())
            .pattern(" S ")
            .pattern("Q Q")
            .pattern(" Q ")
            .define('S', EIOItems.SOULARIUM_INGOT.get())
            .define('Q', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.UNFIRED_DEATH_URN.get())
            .pattern("CPC")
            .pattern("C C")
            .pattern("CCC")
            .define('C', Items.CLAY_BALL)
            .define('P', EIOItems.PULSATING_POWDER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_POWDER.get()))
            .save(recipeConsumer);

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

        ShapedRecipeBuilder
            .shaped(EIOItems.CONDUIT_BINDER_COMPOSITE.get())
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

        ShapedRecipeBuilder
            .shaped(EIOItems.PULSATING_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOItems.PULSATING_ALLOY_NUGGET.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_ALLOY_NUGGET.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.VIBRANT_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOItems.VIBRANT_ALLOY_NUGGET.get())
            .define('D', Tags.Items.GEMS_EMERALD)
            .unlockedBy("has_ingredien", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_ALLOY_NUGGET.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.DYE_INDUSTRIAL_BLEND.get())
            .pattern("LQG")
            .pattern("QBQ")
            .pattern("GQL")
            .define('L', EIOTags.Items.DUSTS_LAPIS)
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('G', EIOItems.DYE_GREEN.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_green", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_GREEN.get()))
            .unlockedBy("has_ingredient_lapis",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOTags.Items.DUSTS_LAPIS).build()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.DYE_ENHANCED_BLEND.get())
            .pattern("PQP")
            .pattern("QBQ")
            .pattern("PQP")
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('P', EIOItems.PULSATING_POWDER.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_powder", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_POWDER.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.DYE_SOUL_ATTUNED_BLEND.get())
            .pattern("SQS")
            .pattern("QBQ")
            .pattern("SQS")
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('S', EIOItems.SOUL_POWDER.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_powder", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOUL_POWDER.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.GEAR_WOOD.get())
            .pattern(" S ")
            .pattern("S S")
            .pattern(" S ")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.GEAR_WOOD.get())
            .pattern("S S")
            .pattern("   ")
            .pattern("S S")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_WOOD.getId().getPath() + "_corner"));

        ShapedRecipeBuilder
            .shaped(EIOItems.GEAR_STONE.get())
            .pattern("NIN")
            .pattern("I I")
            .pattern("NIN")
            .define('N', Tags.Items.RODS_WOODEN)
            .define('I', Tags.Items.COBBLESTONE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.COBBLESTONE).build()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.GEAR_STONE.get())
            .pattern(" I ")
            .pattern("IGI")
            .pattern(" I ")
            .define('I', Tags.Items.COBBLESTONE)
            .define('G', EIOItems.GEAR_WOOD.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GEAR_WOOD.get()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_STONE.getId().getPath() + "_upgrade"));

        upgradeGear(recipeConsumer, EIOItems.GEAR_IRON.get(), EIOItems.GRAINS_OF_INFINITY.get(), Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
        upgradeGear(recipeConsumer, EIOItems.GEAR_ENERGIZED.get(), EIOItems.GEAR_IRON.get(), EIOItems.ENERGETIC_ALLOY_INGOT.get(),
            EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_VIBRANT.get(), EIOItems.GEAR_ENERGIZED.get(), EIOItems.VIBRANT_ALLOY_INGOT.get(),
            EIOItems.VIBRANT_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_DARK_STEEL.get(), EIOItems.GEAR_ENERGIZED.get(), EIOItems.DARK_STEEL_INGOT.get(),
            EIOItems.DARK_STEEL_NUGGET.get());

        ShapedRecipeBuilder
            .shaped(EIOItems.BASIC_CAPACITOR.get())
            .pattern(" NG")
            .pattern("NIN")
            .pattern("GN ")
            .define('N', Tags.Items.NUGGETS_GOLD)
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('I', Tags.Items.INGOTS_COPPER)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .pattern(" I ")
            .pattern("CDC")
            .pattern(" I ")
            .define('I', EIOItems.ENERGETIC_ALLOY_INGOT.get())
            .define('C', EIOItems.BASIC_CAPACITOR.get())
            .define('D', EIOTags.Items.DUSTS_COAL)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.BASIC_CAPACITOR.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.OCTADIC_CAPACITOR.get())
            .pattern(" I ")
            .pattern("CGC")
            .pattern(" I ")
            .define('I', EIOItems.VIBRANT_ALLOY_INGOT.get())
            .define('C', EIOItems.DOUBLE_LAYER_CAPACITOR.get())
            .define('G', Items.GLOWSTONE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DOUBLE_LAYER_CAPACITOR.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOItems.WEATHER_CRYSTAL.get())
            .pattern(" P ")
            .pattern("VEV")
            .pattern(" P ")
            .define('P', EIOItems.PULSATING_CRYSTAL.get())
            .define('V', EIOItems.VIBRANT_CRYSTAL.get())
            .define('E', EIOItems.ENDER_CRYSTAL.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeConsumer);

        ShapelessRecipeBuilder
            .shapeless(EIOItems.ENDERIOS.get())
            .requires(Items.BOWL)
            .requires(Items.MILK_BUCKET)
            .requires(Items.WHEAT)
            .requires(EIOItems.ENDER_FRAGMENT.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(Items.STICK, 16)
            .pattern("W")
            .pattern("W")
            .define('W', ItemTags.LOGS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.LOGS).build()))
            .save(recipeConsumer, EnderIO.loc("stick"));

        grindingBall(recipeConsumer, EIOItems.DARK_STEEL_BALL.get(), EIOItems.DARK_STEEL_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.SOULARIUM_BALL.get(), EIOItems.SOULARIUM_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.CONDUCTIVE_ALLOY_BALL.get(), EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.PULSATING_ALLOY_BALL.get(), EIOItems.PULSATING_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.REDSTONE_ALLOY_BALL.get(), EIOItems.REDSTONE_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.ENERGETIC_ALLOY_BALL.get(), EIOItems.ENERGETIC_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.VIBRANT_ALLOY_BALL.get(), EIOItems.VIBRANT_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.COPPER_ALLOY_BALL.get(), EIOItems.COPPER_ALLOY_INGOT.get());
        grindingBall(recipeConsumer, EIOItems.END_STEEL_BALL.get(), EIOItems.END_STEEL_INGOT.get());
    }

    private void machineReagents(Consumer<FinishedRecipe> recipeConsumer) {
        ShapelessRecipeBuilder
            .shapeless(EIOItems.VOID_REAGENT.get(), 6)
            .requires(Ingredient.of(Tags.Items.GEMS_LAPIS), 2)
            .requires(EIOItems.GRAINS_OF_INFINITY.get(), 3)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);
    }

    private void makeMaterialRecipes(Consumer<FinishedRecipe> recipeConsumer, Item ingot, Item nugget, Block block) {
        ShapelessRecipeBuilder
            .shapeless(ingot, 9)
            .requires(block.asItem())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
            .save(recipeConsumer);
        ShapelessRecipeBuilder
            .shapeless(nugget, 9)
            .requires(ingot)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);
        ShapedRecipeBuilder
            .shaped(block)
            .pattern("III")
            .pattern("III")
            .pattern("III")
            .define('I', ingot)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
            .save(recipeConsumer);
        ShapedRecipeBuilder
            .shaped(ingot)
            .pattern("NNN")
            .pattern("NNN")
            .pattern("NNN")
            .define('N', nugget)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer, nugget.toString() + "_to_ingot");
    }

    private void upgradeGear(Consumer<FinishedRecipe> recipeConsumer, Item resultGear, ItemLike inputGear, ItemLike cross, ItemLike corner) {
        ShapedRecipeBuilder
            .shaped(resultGear)
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
        ShapedRecipeBuilder
            .shaped(resultGear)
            .pattern("NIN")
            .pattern("IGI")
            .pattern("NIN")
            .define('N', corner)
            .define('I', cross)
            .define('G', inputGear)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
            .save(recipeConsumer);
    }

    private void grindingBall(Consumer<FinishedRecipe> recipeConsumer, Item result, ItemLike input) {
        ShapedRecipeBuilder
            .shaped(result, 24)
            .pattern(" I ")
            .pattern("III")
            .pattern(" I ")
            .define('I', input)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(input))
            .save(recipeConsumer);
    }

}
