package com.enderio.machines.datagen.recipe.enchanter;

import com.enderio.base.common.block.glass.*;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.base.common.recipe.EnderFinishedRecipe;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.EIOMachines;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
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
        build(new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.DUSTS_REDSTONE), CountedIngredient.of(Tags.Items.INGOTS_GOLD), CountedIngredient.of(Tags.Items.DUSTS_GLOWSTONE)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.COPPER_ALLOY_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.INGOTS_COPPER), CountedIngredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.VIBRANT_ALLOY_INGOT.get()), List.of(CountedIngredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()), CountedIngredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.REDSTONE_ALLOY_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.DUSTS_REDSTONE), CountedIngredient.of(EIOTags.Items.SILICON)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.CONDUCTIVE_ALLOY_INGOT.get()), List.of(CountedIngredient.of(EIOItems.COPPER_ALLOY_INGOT.get()), CountedIngredient.of(Tags.Items.INGOTS_IRON), CountedIngredient.of(Tags.Items.DUSTS_REDSTONE)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.PULSATING_ALLOY_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.INGOTS_COPPER), CountedIngredient.of(Tags.Items.ENDER_PEARLS)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DARK_STEEL_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.INGOTS_IRON), CountedIngredient.of(EIOTags.Items.DUSTS_COAL), CountedIngredient.of(Tags.Items.OBSIDIAN)), 20000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.SOULARIUM_INGOT.get()), List.of(CountedIngredient.of(Items.SOUL_SAND, Items.SOUL_SOIL), CountedIngredient.of(Tags.Items.INGOTS_GOLD)), 10000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.END_STEEL_INGOT.get()), List.of(CountedIngredient.of(Tags.Items.END_STONES), CountedIngredient.of(EIOItems.DARK_STEEL_INGOT.get()), CountedIngredient.of(Tags.Items.OBSIDIAN)), 20000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.NETHERCOTTA.get()), List.of(CountedIngredient.of(Tags.Items.INGOTS_NETHER_BRICK), CountedIngredient.of(4, Items.NETHER_WART), CountedIngredient.of(6, Items.CLAY_BALL)), 20000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Dusts

        // TODO: These are just smelting recipes, prolly reading the old JEI isnt a good idea
        //        build(new ItemStack(Items.IRON_INGOT), List.of(EnderIngredient.of(EIOTags.Items.DUSTS_IRON)), 2000, 0.3f, pFinishedRecipeConsumer);
        //        build(new ItemStack(Items.GOLD_INGOT), List.of(EnderIngredient.of(EIOTags.Items.DUSTS_GOLD)), 2000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Dyes

        build(new ItemStack(EIOItems.DYE_GREEN.get()), List.of(CountedIngredient.of(Tags.Items.DYES_GREEN), CountedIngredient.of(Items.EGG), CountedIngredient.of(EIOTags.Items.DUSTS_COAL)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get(), 2), "double", List.of(CountedIngredient.of(2, Tags.Items.DYES_GREEN), CountedIngredient.of(Tags.Items.SLIMEBALLS), CountedIngredient.of(2, EIOTags.Items.DUSTS_COAL)), 2000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get()), "clippings", List.of(CountedIngredient.of(6, EIOItems.PLANT_MATTER_GREEN.get()), CountedIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_GREEN.get(), 2), "double_clippings", List.of(CountedIngredient.of(12, EIOItems.PLANT_MATTER_GREEN.get()), CountedIngredient.of(Tags.Items.SLIMEBALLS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DYE_BROWN.get()), List.of(CountedIngredient.of(Items.BROWN_DYE), CountedIngredient.of(Items.EGG), CountedIngredient.of(EIOTags.Items.DUSTS_COAL)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get(), 2), "double", List.of(CountedIngredient.of(2, Items.BROWN_DYE), CountedIngredient.of(Tags.Items.SLIMEBALLS), CountedIngredient.of(2, EIOTags.Items.DUSTS_COAL)), 2000, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get()), "twigs", List.of(CountedIngredient.of(6, EIOItems.PLANT_MATTER_BROWN.get()), CountedIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BROWN.get(), 2), "twigs_double", List.of(CountedIngredient.of(12, EIOItems.PLANT_MATTER_BROWN.get()), CountedIngredient.of(Tags.Items.SLIMEBALLS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DYE_BLACK.get()), List.of(CountedIngredient.of(3, EIOTags.Items.DUSTS_COAL), CountedIngredient.of(Items.EGG)), 1500, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOItems.DYE_BLACK.get(), 2), "double", List.of(CountedIngredient.of(6, EIOTags.Items.DUSTS_COAL), CountedIngredient.of(Tags.Items.SLIMEBALLS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.RED_DYE, 12), List.of(CountedIngredient.of(Items.BEETROOT), CountedIngredient.of(3, Items.CLAY_BALL), CountedIngredient.of(6, Items.EGG)), 15000, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Chassis

        build(new ItemStack(EIOBlocks.INDUSTRIAL_MACHINE_CHASSIS.get()), List.of(CountedIngredient.of(EIOBlocks.SIMPLE_MACHINE_CHASSIS.get()), CountedIngredient.of(EIOItems.DYE_INDUSTRIAL_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.ENHANCED_MACHINE_CHASSIS.get()), List.of(CountedIngredient.of(EIOBlocks.END_STEEL_MACHINE_CHASSIS.get()), CountedIngredient.of(EIOItems.DYE_ENHANCED_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);
        build(new ItemStack(EIOBlocks.SOUL_MACHINE_CHASSIS.get()), List.of(CountedIngredient.of(EIOBlocks.SIMPLE_MACHINE_CHASSIS.get()), CountedIngredient.of(EIOItems.DYE_SOUL_ATTUNED_BLEND.get())), 3600, 0.3f, pFinishedRecipeConsumer);

        // endregion

        // region Glass

        for (Map.Entry<GlassIdentifier, GlassBlocks> glassGroup : EIOBlocks.GLASS_BLOCKS.entrySet()) {
            GlassIdentifier identifier = glassGroup.getKey();
            if (identifier.collisionPredicate() == GlassCollisionPredicate.NONE) {
                FusedQuartzBlock clear = glassGroup.getValue().CLEAR.get();
                var mainIngredient = identifier.explosion_resistance() ? CountedIngredient.of(4, Tags.Items.GEMS_QUARTZ) : CountedIngredient.of(Tags.Items.GLASS_COLORLESS);
                @Nullable
                var altIngredient = identifier.explosion_resistance() ? CountedIngredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ) : null;
                var energy = identifier.explosion_resistance() ? 5000 : 2500;
                if (identifier.lighting() == GlassLighting.NONE) {
                    glass(clear, mainIngredient, altIngredient, energy, 0.3f, pFinishedRecipeConsumer);
                } else {
                    var composite = identifier.lighting() == GlassLighting.EMITTING ? CountedIngredient.of(4, Tags.Items.DUSTS_GLOWSTONE) : CountedIngredient.of(4, Tags.Items.GEMS_AMETHYST);
                    var compositeB = identifier.lighting() == GlassLighting.EMITTING ? CountedIngredient.of(Blocks.GLOWSTONE) : CountedIngredient.of(Tags.Items.STORAGE_BLOCKS_AMETHYST);
                    compositeGlass(clear, "from_main", mainIngredient, composite, compositeB, energy, 0.3f, pFinishedRecipeConsumer);
                    if (altIngredient != null)
                        compositeGlass(clear, "from_storage", altIngredient, composite, compositeB, energy, 0.3f, pFinishedRecipeConsumer);

                    Block withoutLight = EIOBlocks.GLASS_BLOCKS.get(identifier.withoutLight()).CLEAR.get();
                    compositeGlass(clear,"from_base", CountedIngredient.of(withoutLight), composite, compositeB, energy/2, 0.3f, pFinishedRecipeConsumer);
                }
            }
        }

        // endregion

        // region Misc

        build(new ItemStack(EIOItems.CAKE_BASE.get(), 2), List.of(CountedIngredient.of(3, EIOItems.FLOUR.get()), CountedIngredient.of(Items.EGG)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.COOKIE, 8), List.of(CountedIngredient.of(EIOItems.FLOUR.get()), CountedIngredient.of(Items.COCOA_BEANS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.PHOTOVOLTAIC_PLATE.get()), List.of(CountedIngredient.of(3, EIOItems.PHOTOVOLTAIC_COMPOSITE.get())), 15000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.ENDER_PEARL), List.of(CountedIngredient.of(9, EIOTags.Items.DUSTS_ENDER)), 2000, 0.3f, pFinishedRecipeConsumer);

        // TODO: Infinity reagent
        //        build(new ItemStack(), List.of(EnderIngredient.of(EIOItems.GRAINS_OF_INFINITY.get()), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 5000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(Items.DEAD_BUSH), List.of(CountedIngredient.of(ItemTags.SAPLINGS)), 2000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()), List.of(CountedIngredient.of(EIOBlocks.DARK_STEEL_BARS.get()), CountedIngredient.of(Items.CLAY_BALL), CountedIngredient.of(4, Tags.Items.STRING)), 30000, 0.3f, pFinishedRecipeConsumer);

        build(new ItemStack(EIOItems.CLAYED_GLOWSTONE.get(), 2), List.of(CountedIngredient.of(Tags.Items.DUSTS_GLOWSTONE), CountedIngredient.of(Items.CLAY_BALL)), 5000, 0.3f, pFinishedRecipeConsumer);

        // endregion
    }

    protected void glass(FusedQuartzBlock block, CountedIngredient input, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new ItemStack(block), List.of(input), energy, experience, recipeConsumer);
    }

    protected void glass(FusedQuartzBlock block, CountedIngredient input, @Nullable CountedIngredient inputAlt, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new ItemStack(block), List.of(input), energy, experience, recipeConsumer);
        if (inputAlt != null)
            build(new ItemStack(block), "alt", List.of(inputAlt), energy, experience, recipeConsumer);
    }

    protected void compositeGlass(FusedQuartzBlock block, String suffix, CountedIngredient inputA, CountedIngredient inputB, CountedIngredient inputBAlt, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new ItemStack(block), suffix, List.of(inputA, inputB), energy, experience, recipeConsumer);
        build(new ItemStack(block), suffix + "_alt", List.of(inputA, inputBAlt), energy, experience, recipeConsumer);
    }

    protected void build(ItemStack result, List<CountedIngredient> ingredients, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new AlloySmeltingRecipeImpl(null, ingredients, result, energy, experience), result.getItem().getRegistryName().getPath(), recipeConsumer);
    }

    protected void build(ItemStack result, String suffix, List<CountedIngredient> ingredients, int energy, float experience, Consumer<FinishedRecipe> recipeConsumer) {
        build(new AlloySmeltingRecipeImpl(null, ingredients, result, energy, experience), result.getItem().getRegistryName().getPath() + "_" + suffix, recipeConsumer);
    }

    protected void build(AlloySmeltingRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new EnderFinishedRecipe<>(recipe, EIOMachines.MODID, name));
    }
}