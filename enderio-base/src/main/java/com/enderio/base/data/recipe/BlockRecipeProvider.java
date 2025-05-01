package com.enderio.base.data.recipe;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.block.ResettingLeverBlock;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class BlockRecipeProvider extends RecipeProvider {

    public BlockRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        addPressurePlateRecipes(recipeOutput);
        addLeverRecipes(recipeOutput);
        addConstructionBlockRecipes(recipeOutput);
        buildChassisRecipes(recipeOutput);
        buildBuildingRecipes(recipeOutput);
    }

    private void buildChassisRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.VOID_CHASSIS.get())
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern("IGI")
                .pattern("G G")
                .pattern("IGI")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EIOBlocks.ENSOULED_CHASSIS.get())
                .define('C', EIOBlocks.SOUL_CHAIN.get())
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .pattern("CIC")
                .pattern("IQI")
                .pattern("CIC")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
                .save(recipeOutput);
    }

    private void buildBuildingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.SOUL_CHAIN.get(), 2)
                .define('Q', EIOTags.Items.DUSTS_QUARTZ)
                .define('N', EIOTags.Items.NUGGETS_SOULARIUM)
                .define('I', EIOTags.Items.INGOTS_SOULARIUM)
                .pattern(" N ")
                .pattern("QIQ")
                .pattern(" N ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
                .save(recipeOutput);
    }

    private void addConstructionBlockRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_LADDER.get(), 12)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" I ")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_BARS.get(), 16)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("III")
                .pattern("III")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_TRAPDOOR, 1)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("II")
                .pattern("II")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.DARK_STEEL_DOOR, 3)
                .define('I', EIOTags.Items.INGOTS_DARK_STEEL)
                .pattern("II")
                .pattern("II")
                .pattern("II")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.END_STEEL_BARS, 12)
                .define('I', EIOTags.Items.INGOTS_END_STEEL)
                .pattern("III")
                .pattern("III")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.END_STEEL_INGOT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, EIOBlocks.REINFORCED_OBSIDIAN.get())
                .define('B', EIOBlocks.DARK_STEEL_BARS)
                .define('G', EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .define('O', Tags.Items.OBSIDIANS)
                .pattern("GBG")
                .pattern("BOB")
                .pattern("GBG")
                .unlockedBy("has_ingredient",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
                .save(recipeOutput);
    }

    private void addPressurePlateRecipes(RecipeOutput recipeOutput) {
        // eio plates
        addPressurePlateRecipe(recipeOutput, EIOBlocks.DARK_STEEL_PRESSURE_PLATE, EIOTags.Items.INGOTS_DARK_STEEL,
                EIOItems.DARK_STEEL_INGOT);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE,
                EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        addPressurePlateRecipe(recipeOutput, EIOBlocks.SOULARIUM_PRESSURE_PLATE, EIOTags.Items.INGOTS_SOULARIUM,
                EIOItems.SOULARIUM_INGOT);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE,
                EIOBlocks.SOULARIUM_PRESSURE_PLATE);
        // wooden silent plates
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_OAK_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE,
                Blocks.ACACIA_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE,
                Blocks.DARK_OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE,
                Blocks.SPRUCE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE,
                Blocks.JUNGLE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE,
                Blocks.CRIMSON_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_WARPED_PRESSURE_PLATE,
                Blocks.WARPED_PRESSURE_PLATE);
        // stone silent plates
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE,
                Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        // wighted silent plates
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE,
                Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeOutput, EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE,
                Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    private void addPressurePlateRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result,
            TagKey<Item> ingredient, ItemLike trigger) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('#', ingredient)
                .pattern("##")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(trigger))
                .save(recipeOutput);
    }

    private void addPressurePlateRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result,
            ItemLike ingredient) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('#', ingredient)
                .pattern("##")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(recipeOutput);
    }

    private void addSilentPressurePlateRecipe(RecipeOutput recipeOutput, Supplier<? extends Block> result,
            ItemLike ingredient) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result.get().asItem())
                .define('W', ItemTags.WOOL)
                .define('P', ingredient)
                .pattern("W")
                .pattern("P")
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(recipeOutput);
    }

    private void addLeverRecipes(RecipeOutput recipeOutput) {
        addLeverRecipe(recipeOutput, EIOBlocks.RESETTING_LEVER_FIVE, EIOBlocks.RESETTING_LEVER_FIVE_INV, null, null, 1);
        addLeverRecipe(recipeOutput, EIOBlocks.RESETTING_LEVER_TEN, EIOBlocks.RESETTING_LEVER_TEN_INV,
                EIOBlocks.RESETTING_LEVER_FIVE, EIOBlocks.RESETTING_LEVER_FIVE_INV, 2);
        addLeverRecipe(recipeOutput, EIOBlocks.RESETTING_LEVER_THIRTY, EIOBlocks.RESETTING_LEVER_THIRTY_INV,
                EIOBlocks.RESETTING_LEVER_TEN, EIOBlocks.RESETTING_LEVER_TEN_INV, 3);
        addLeverRecipe(recipeOutput, EIOBlocks.RESETTING_LEVER_SIXTY, EIOBlocks.RESETTING_LEVER_SIXTY_INV,
                EIOBlocks.RESETTING_LEVER_THIRTY, EIOBlocks.RESETTING_LEVER_THIRTY_INV, 4);
        addLeverRecipe(recipeOutput, EIOBlocks.RESETTING_LEVER_THREE_HUNDRED,
                EIOBlocks.RESETTING_LEVER_THREE_HUNDRED_INV, EIOBlocks.RESETTING_LEVER_SIXTY,
                EIOBlocks.RESETTING_LEVER_SIXTY_INV, 5);
    }

    private void addLeverRecipe(RecipeOutput recipeOutput, Supplier<? extends ResettingLeverBlock> base,
            Supplier<? extends ResettingLeverBlock> inverted,
            @Nullable Supplier<? extends ResettingLeverBlock> previous,
            @Nullable Supplier<? extends ResettingLeverBlock> previousInverted, int numRedstone) {

        // Get name of each lever.
        String baseName = BuiltInRegistries.BLOCK.getKey(base.get()).getPath();
        String invertedName = BuiltInRegistries.BLOCK.getKey(inverted.get()).getPath();

        // Main recipe.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, base.get())
                .requires(Blocks.LEVER)
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE), numRedstone)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(recipeOutput);

        // Un-invert inverted.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, base.get())
                .requires(inverted.get())
                .requires(Blocks.REDSTONE_TORCH)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(recipeOutput, EnderIO.loc(baseName + "_from_inv"));

        // Previous upgrade recipe
        if (previous != null) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, base.get())
                    .requires(previous.get())
                    .requires(Tags.Items.DUSTS_REDSTONE)
                    .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                    .save(recipeOutput, EnderIO.loc(baseName + "_from_prev"));
        }

        // Main inverted recipe.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, inverted.get())
                .requires(Blocks.LEVER)
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE), numRedstone)
                .requires(Blocks.REDSTONE_TORCH)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(recipeOutput);

        // Invert base.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, inverted.get())
                .requires(base.get())
                .requires(Blocks.REDSTONE_TORCH)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                .save(recipeOutput, EnderIO.loc(invertedName + "_from_base"));

        // Previous upgrade recipe
        if (previousInverted != null) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, inverted.get())
                    .requires(previousInverted.get())
                    .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                    .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
                    .save(recipeOutput, EnderIO.loc(invertedName + "_from_prev"));
        }
    }
}
