package com.enderio.machines.datagen.recipe.enchanter;

import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.recipe.EnderIngredient;
import com.enderio.base.common.recipe.EnderIngredient;
import com.enderio.base.common.recipe.EnderRecipeResult;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;

public class AlloyRecipeGenerator extends RecipeProvider {

    public AlloyRecipeGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        // TODO: Review all recipes and alloy compositions
        // TODO: Experience values need set properly, i just used a filler value off the top of my head

        // region Metal Alloys

        // TODO: Re-enable new alloys once we re-apply those changes
        build(new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.DUSTS_REDSTONE), EnderIngredient.of(Tags.Items.INGOTS_GOLD), EnderIngredient.of(Tags.Items.DUSTS_GLOWSTONE)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.COPPER_ALLOY_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.INGOTS_COPPER), EnderIngredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.VIBRANT_ALLOY_INGOT.get()), List.of(EnderIngredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()), EnderIngredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.REDSTONE_ALLOY_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.DUSTS_REDSTONE), EnderIngredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.CONDUCTIVE_ALLOY_INGOT.get()), List.of(EnderIngredient.of(EIOItems.COPPER_ALLOY_INGOT.get()), EnderIngredient.of(Tags.Items.INGOTS_IRON), EnderIngredient.of(Tags.Items.DUSTS_REDSTONE)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.PULSATING_ALLOY_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.INGOTS_COPPER), EnderIngredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DARK_STEEL_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.INGOTS_IRON), EnderIngredient.of(EIOTags.Items.DUSTS_COAL), EnderIngredient.of(Tags.Items.OBSIDIAN)), 20000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.SOULARIUM_INGOT.get()), List.of(EnderIngredient.of(Items.SOUL_SAND, Items.SOUL_SOIL), EnderIngredient.of(Tags.Items.INGOTS_GOLD)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.END_STEEL_INGOT.get()), List.of(EnderIngredient.of(Tags.Items.END_STONES), EnderIngredient.of(EIOItems.DARK_STEEL_INGOT.get()), EnderIngredient.of(Tags.Items.OBSIDIAN)), 20000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.NETHERCOTTA.get()), List.of(EnderIngredient.of(Tags.Items.INGOTS_NETHER_BRICK), EnderIngredient.of(4, Items.NETHER_WART), EnderIngredient.of(6, Items.CLAY_BALL)), 20000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Dusts

        // TODO: These are just smelting recipes, prolly reading the old JEI isnt a good idea
        //        build(new ItemStack(Items.IRON_INGOT), List.of(EnderIngredient.of(EIOTags.Items.DUSTS_IRON)), 2000, 0.3f, pFinishedRecipeConsumer);
        //        build(new ItemStack(Items.GOLD_INGOT), List.of(EnderIngredient.of(EIOTags.Items.DUSTS_GOLD)), 2000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Dyes

        build(new ItemStack(EIOItems.DYE_GREEN.get()), List.of(EnderIngredient.of(Items.GREEN_DYE), EnderIngredient.of(Items.EGG), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get(), 2), "double", List.of(EnderIngredient.of(2, Items.GREEN_DYE), EnderIngredient.of(Items.SLIME_BALL), EnderIngredient.of(2, EIOTags.Items.DUSTS_COAL)), 2000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get()), "clippings", List.of(EnderIngredient.of(6, EIOItems.PLANT_MATTER_GREEN.get()), EnderIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get(), 2), "double_clippings", List.of(EnderIngredient.of(12, EIOItems.PLANT_MATTER_GREEN.get()), EnderIngredient.of(Items.SLIME_BALL)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DYE_BROWN.get()), List.of(EnderIngredient.of(Items.BROWN_DYE), EnderIngredient.of(Items.EGG), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get(), 2), "double", List.of(EnderIngredient.of(2, Items.BROWN_DYE), EnderIngredient.of(Items.SLIME_BALL), EnderIngredient.of(2, EIOTags.Items.DUSTS_COAL)), 2000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get()), "twigs", List.of(EnderIngredient.of(6, EIOItems.PLANT_MATTER_BROWN.get()), EnderIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get(), 2), "twigs_double", List.of(EnderIngredient.of(12, EIOItems.PLANT_MATTER_BROWN.get()), EnderIngredient.of(Items.SLIME_BALL)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DYE_BLACK.get()), List.of(EnderIngredient.of(3, EIOTags.Items.DUSTS_COAL), EnderIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BLACK.get(), 2), "double", List.of(EnderIngredient.of(6, EIOTags.Items.DUSTS_COAL), EnderIngredient.of(Items.SLIME_BALL)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.RED_DYE, 12), List.of(EnderIngredient.of(Items.BEETROOT), EnderIngredient.of(3, Items.CLAY_BALL), EnderIngredient.of(6, Items.EGG)), 15000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Chassis

        build(new ItemStack(EIOBlocks.INDUSTRIAL_MACHINE_CHASSIS.get()), List.of(EnderIngredient.of(EIOBlocks.SIMPLE_MACHINE_CHASSIS.get()), EnderIngredient.of(EIOItems.DYE_INDUSTRIAL_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.ENHANCED_MACHINE_CHASSIS.get()), List.of(EnderIngredient.of(EIOBlocks.END_STEEL_MACHINE_CHASSIS.get()), EnderIngredient.of(EIOItems.DYE_ENHANCED_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.SOUL_MACHINE_CHASSIS.get()), List.of(EnderIngredient.of(EIOBlocks.SIMPLE_MACHINE_CHASSIS.get()), EnderIngredient.of(EIOItems.DYE_SOUL_ATTUNED_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Misc

        build(new ItemStack(EIOItems.CAKE_BASE.get(), 2), List.of(EnderIngredient.of(3, EIOItems.FLOUR.get()), EnderIngredient.of(Items.EGG)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.COOKIE, 8), List.of(EnderIngredient.of(EIOItems.FLOUR.get()), EnderIngredient.of(Items.COCOA_BEANS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOBlocks.QUITE_CLEAR_GLASS.CLEAR.get()), List.of(EnderIngredient.of(Tags.Items.GLASS_COLORLESS)), 2500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.FUSED_QUARTZ.CLEAR.get()), List.of(EnderIngredient.of(4, Tags.Items.GEMS_QUARTZ)), 5000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.FUSED_QUARTZ.CLEAR.get()), "block", List.of(EnderIngredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ)), 5000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.PHOTOVOLTAIC_PLATE.get()), List.of(EnderIngredient.of(3, EIOItems.PHOTOVOLTAIC_COMPOSITE.get())), 15000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.ENDER_PEARL), List.of(EnderIngredient.of(9, EIOItems.ENDER_PEARL_POWDER.get())), 2000, 0.3f, pFinishedRecipeConsumer);

        // TODO: Infinity reagent
        //        build(new ItemStack(), List.of(EnderIngredient.of(EIOItems.GRAINS_OF_INFINITY.get()), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 5000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.DEAD_BUSH), List.of(EnderIngredient.of(ItemTags.SAPLINGS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()), List.of(EnderIngredient.of(EIOBlocks.DARK_STEEL_BARS.get()), EnderIngredient.of(Items.CLAY_BALL), EnderIngredient.of(4, Items.STRING)), 30000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.CLAYED_GLOWSTONE.get(), 2), List.of(EnderIngredient.of(Tags.Items.DUSTS_GLOWSTONE), EnderIngredient.of(Items.CLAY_BALL)), 5000, 0.3f, pFinishedRecipeConsumer);

        // endregion
    }

    protected void build(ItemStack result, List<EnderIngredient> ingredients, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new AlloySmeltingRecipe(null, ingredients, result, energy, experience), result.getItem().getRegistryName().getPath(), recipeConsumer);
    }

    protected void build(ItemStack result, String suffix, List<EnderIngredient> ingredients, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new AlloySmeltingRecipe(null, ingredients, result, energy, experience), result.getItem().getRegistryName().getPath() + "_" + suffix, recipeConsumer);
    }

    protected void build(AlloySmeltingRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new EnderRecipeResult<>(recipe, EIOMachines.MODID, name));
    }
}