package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.block.ResettingLeverBlock;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BlockRecipes extends RecipeProvider {
    public BlockRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        addPressurePlateRecipes(recipeConsumer);
        addLeverRecipes(recipeConsumer);
        addConstructionBlockRecipes(recipeConsumer);
        buildChassisRecipes(recipeConsumer);
        buildBuildingRecipes(recipeConsumer);
    }

    private void buildChassisRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.VOID_CHASSIS.get())
            .define('B', Blocks.IRON_BARS)
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("BIB")
            .pattern("IGI")
            .pattern("BIB")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.ENSOULED_CHASSIS.get())
            .define('C', EIOBlocks.SOUL_CHAIN.get())
            .define('Q', Tags.Items.GEMS_QUARTZ)
            .define('I', EIOItems.SOULARIUM_INGOT.get())
            .pattern("CIC")
            .pattern("IQI")
            .pattern("CIC")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
            .save(recipeConsumer);
    }

    private void buildBuildingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.SOUL_CHAIN.get(), 2)
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('N', EIOItems.SOULARIUM_NUGGET.get())
            .define('I', EIOItems.SOULARIUM_INGOT.get())
            .pattern(" N ")
            .pattern("QIQ")
            .pattern(" N ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
            .save(recipeConsumer);
    }

    private void addConstructionBlockRecipes(Consumer<FinishedRecipe> recipeConsumer) {

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_LADDER.get(), 12)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern(" I ")
            .pattern(" I ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_BARS.get(), 16)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_TRAPDOOR.get(), 1)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("II")
            .pattern("II")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_DOOR.get(), 3)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("II")
            .pattern("II")
            .pattern("II")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.END_STEEL_BARS.get(), 12)
            .define('I', EIOItems.END_STEEL_INGOT.get())
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.END_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.REINFORCED_OBSIDIAN.get())
            .define('B', EIOBlocks.DARK_STEEL_BARS.get())
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('O', Tags.Items.OBSIDIAN)
            .pattern("GBG")
            .pattern("BOB")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);
    }

    private void addPressurePlateRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        //eio plates
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.DARK_STEEL_PRESSURE_PLATE, EIOItems.DARK_STEEL_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE, EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.SOULARIUM_PRESSURE_PLATE, EIOItems.SOULARIUM_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE, EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        //wooden silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_OAK_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_WARPED_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE);
        //stone silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        //wighted silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    private void addPressurePlateRecipe(Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
            .define('#', ingredient)
            .pattern("##")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
    }

    private void addSilentPressurePlateRecipe(Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
            .define('W', ItemTags.WOOL)
            .define('P', ingredient)
            .pattern("W")
            .pattern("P")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
    }

    private void addLeverRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_FIVE, EIOBlocks.RESETTING_LEVER_FIVE_INV, null, null, 1);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_TEN, EIOBlocks.RESETTING_LEVER_TEN_INV, EIOBlocks.RESETTING_LEVER_FIVE,
            EIOBlocks.RESETTING_LEVER_FIVE_INV, 2);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_THIRTY, EIOBlocks.RESETTING_LEVER_THIRTY_INV, EIOBlocks.RESETTING_LEVER_TEN,
            EIOBlocks.RESETTING_LEVER_TEN_INV, 3);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_SIXTY, EIOBlocks.RESETTING_LEVER_SIXTY_INV, EIOBlocks.RESETTING_LEVER_THIRTY,
            EIOBlocks.RESETTING_LEVER_THIRTY_INV, 4);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_THREE_HUNDRED, EIOBlocks.RESETTING_LEVER_THREE_HUNDRED_INV, EIOBlocks.RESETTING_LEVER_SIXTY,
            EIOBlocks.RESETTING_LEVER_SIXTY_INV, 5);
    }

    private void addLeverRecipe(Consumer<FinishedRecipe> finishedRecipeConsumer, BlockEntry<? extends ResettingLeverBlock> base,
        BlockEntry<? extends ResettingLeverBlock> inverted, @Nullable BlockEntry<? extends ResettingLeverBlock> previous,
        @Nullable BlockEntry<? extends ResettingLeverBlock> previousInverted, int numRedstone) {

        // Get name of each lever.
        String baseName = base.getId().getPath();
        String invertedName = inverted.getId().getPath();

        // Main recipe.
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.REDSTONE, base.get())
            .requires(Blocks.LEVER)
            .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE), numRedstone)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
            .save(finishedRecipeConsumer);

        // Un-invert inverted.
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.REDSTONE, base.get())
            .requires(inverted.get())
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
            .save(finishedRecipeConsumer, EnderIO.loc(baseName + "_from_inv"));

        // Previous upgrade recipe
        if (previous != null) {
            ShapelessRecipeBuilder
                .shapeless(RecipeCategory.REDSTONE, base.get())
                .requires(previous.get())
                .requires(Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(finishedRecipeConsumer, EnderIO.loc(baseName + "_from_prev"));
        }

        // Main inverted recipe.
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.REDSTONE, inverted.get())
            .requires(Blocks.LEVER)
            .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE), numRedstone)
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
            .save(finishedRecipeConsumer);

        // Invert base.
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.REDSTONE, inverted.get())
            .requires(base.get())
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
            .save(finishedRecipeConsumer, EnderIO.loc(invertedName + "_from_base"));

        // Previous upgrade recipe
        if (previousInverted != null) {
            ShapelessRecipeBuilder
                .shapeless(RecipeCategory.REDSTONE, inverted.get())
                .requires(previousInverted.get())
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(finishedRecipeConsumer, EnderIO.loc(invertedName + "_from_prev"));
        }

    }

}
