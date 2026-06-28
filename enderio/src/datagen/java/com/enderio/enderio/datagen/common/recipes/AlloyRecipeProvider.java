package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.content.glass.FusedQuartzBlock;
import com.enderio.enderio.content.glass.GlassBlocks;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.content.glass.GlassIdentifier;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AlloyRecipeProvider extends SubRecipeProvider {

    private HolderLookup.RegistryLookup<Item> items;

    protected SizedIngredient sizedFromTag(TagKey<Item> tag, int count) {
        return new SizedIngredient(Ingredient.of(this.items.getOrThrow(tag)), count);
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        this.items = registries.lookupOrThrow(Registries.ITEM);
        // TODO: Review all recipes and alloy compositions
        // TODO: Experience values need set properly, i just used a filler value off the
        // top of my head

        // region Metal Alloys

        build(new ItemStackTemplate(EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), 2),
                List.of(sizedFromTag(Tags.Items.INGOTS_IRON, 1),
                    sizedFromTag(Tags.Items.INGOTS_COPPER, 1)),
                3200, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.ENERGETIC_ALLOY_INGOT.get(), 2),
                List.of(sizedFromTag(Tags.Items.DUSTS_REDSTONE, 1),
                        sizedFromTag(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY, 1),
                        sizedFromTag(Tags.Items.INGOTS_GOLD, 1)),
                4800, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.VIBRANT_ALLOY_INGOT.get(), 2),
                List.of(sizedFromTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY, 1),
                        sizedFromTag(Tags.Items.ENDER_PEARLS, 1),
                        sizedFromTag(Tags.Items.DUSTS_GLOWSTONE, 1)),
                5600, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.REDSTONE_ALLOY_INGOT.get()),
                List.of(sizedFromTag(Tags.Items.DUSTS_REDSTONE, 1), sizedFromTag(Tags.Items.INGOTS_COPPER, 1)),
                3200, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.PULSATING_ALLOY_INGOT.get(), 2),
                List.of(sizedFromTag(Tags.Items.INGOTS_IRON, 1), sizedFromTag(Tags.Items.ENDER_PEARLS, 1)),
                4800, 0.3f, recipeOutput);
        build(EnderIO.id("dark_steel_ingot_with_coal"),
                List.of(sizedFromTag(Tags.Items.INGOTS_IRON, 1), SizedIngredient.of(Items.COAL, 2),
                    sizedFromTag(Tags.Items.OBSIDIANS, 1)),
                new ItemStackTemplate(EIOItems.DARK_STEEL_INGOT.get(), 2), 3200, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.DARK_STEEL_INGOT.get(), 2), List.of(sizedFromTag(Tags.Items.INGOTS_IRON, 1),
                sizedFromTag(EIOTags.Items.DUSTS_COAL, 1), sizedFromTag(Tags.Items.OBSIDIANS, 1)), 3200,
                0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.SOULARIUM_INGOT.get()),
                List.of(new SizedIngredient(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL), 1),
                    sizedFromTag(Tags.Items.INGOTS_GOLD, 1)),
                5600, 0.3f, recipeOutput);
        build(new ItemStackTemplate(EIOItems.END_STEEL_INGOT.get(), 2), List.of(sizedFromTag(Tags.Items.END_STONES, 1),
                sizedFromTag(EIOTags.Items.INGOTS_DARK_STEEL, 1), sizedFromTag(Tags.Items.OBSIDIANS, 1)),
                6400, 0.3f, recipeOutput);

        // endregion

        // TODO: Balance below energies:

        // region Dyes

        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.GREEN)), "clippings",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_GREEN.get(), 6), sizedFromTag(ItemTags.EGGS, 1)),
                1000, 0.3f, recipeOutput);
        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.GREEN), 2), "double_clippings",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_GREEN.get(), 12),
                    sizedFromTag(Tags.Items.SLIME_BALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.BROWN)), "twigs",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_BROWN.get(), 6), sizedFromTag(ItemTags.EGGS, 1)),
                1000, 0.3f, recipeOutput);
        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.BROWN), 2), "twigs_double",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_BROWN.get(), 12),
                        sizedFromTag(Tags.Items.SLIME_BALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.BLACK)),
                List.of(sizedFromTag(EIOTags.Items.DUSTS_COAL, 1), sizedFromTag(ItemTags.EGGS, 1)), 1000, 0.3f,
                recipeOutput);
        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.BLACK), 2), "double",
                List.of(sizedFromTag(EIOTags.Items.DUSTS_COAL, 2), sizedFromTag(Tags.Items.SLIME_BALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStackTemplate(Items.DYE.pick(DyeColor.RED), 12), List.of(SizedIngredient.of(Items.BEETROOT, 1),
                SizedIngredient.of(Items.CLAY_BALL, 3), sizedFromTag(ItemTags.EGGS, 6)), 1600, 0.3f, recipeOutput);

        // endregion

        // region Glass

        for (Map.Entry<GlassIdentifier, GlassBlocks> glassGroup : EIOBlocks.GLASS_BLOCKS.entrySet()) {
            GlassIdentifier identifier = glassGroup.getKey();
            if (identifier.collisionPredicate() == GlassCollisionPredicate.NONE) {
                FusedQuartzBlock clear = glassGroup.getValue().CLEAR.get();
                var mainIngredient = identifier.explosionResistance() ? sizedFromTag(Tags.Items.GEMS_QUARTZ, 4)
                        : sizedFromTag(Tags.Items.GLASS_BLOCKS_COLORLESS, 1);
                var altIngredient = identifier.explosionResistance()
                        ? sizedFromTag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ, 1)
                        : null;
                var energy = identifier.explosionResistance() ? 6400 : 3200;
                if (identifier.lighting() == GlassLighting.NONE) {
                    glass(clear, mainIngredient, altIngredient, energy, 0.3f, recipeOutput);
                } else {
                    var composite = identifier.lighting() == GlassLighting.EMITTING
                            ? sizedFromTag(Tags.Items.DUSTS_GLOWSTONE, 4)
                            : sizedFromTag(Tags.Items.GEMS_AMETHYST, 4);
                    var compositeB = identifier.lighting() == GlassLighting.EMITTING
                            ? SizedIngredient.of(Blocks.GLOWSTONE, 1)
                            : sizedFromTag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST, 1);
                    compositeGlass(clear, "from_main", mainIngredient, composite, compositeB, energy, 0.3f,
                            recipeOutput);
                    if (altIngredient != null) {
                        compositeGlass(clear, "from_storage", altIngredient, composite, compositeB, energy, 0.3f,
                                recipeOutput);
                    }

                    Block withoutLight = EIOBlocks.GLASS_BLOCKS.get(identifier.withoutLight()).CLEAR.get();
                    compositeGlass(clear, "from_base", SizedIngredient.of(withoutLight, 1), composite, compositeB,
                            energy / 2, 0.3f, recipeOutput);
                }
            }
        }

        // endregion

        // region Misc

        build(new ItemStackTemplate(EIOItems.PHOTOVOLTAIC_PLATE.get()),
                List.of(SizedIngredient.of(EIOItems.PHOTOVOLTAIC_COMPOSITE.get(), 2)), 5600, 0.3f, recipeOutput);

        build(new ItemStackTemplate(Items.ENDER_PEARL), List.of(sizedFromTag(EIOTags.Items.DUSTS_ENDER, 9)), 2000, 0.3f,
                recipeOutput);

//        build(new ItemStackTemplate(), List.of(EnderIngredient.of(EIOItems.GRAINS_OF_INFINITY.get()), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 5000, 0.3f, recipeOutput);

        build(new ItemStackTemplate(Items.DEAD_BUSH), List.of(sizedFromTag(ItemTags.SAPLINGS, 1)), 500, 0.3f,
                recipeOutput);

//        build(new ItemStackTemplate(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()), List.of(SizedIngredient.of(EIOBlocks.DARK_STEEL_BARS.get()), SizedIngredient.of(Items.CLAY_BALL), SizedIngredient.of(4, Tags.Items.STRING)), 30000, 0.3f, recipeOutput);

        build(new ItemStackTemplate(EIOBlocks.INDUSTRIAL_INSULATION.asItem()),
                List.of(sizedFromTag(EIOTags.Items.DUSTS_LAPIS, 1), sizedFromTag(ItemTags.WOOL, 1),
                    sizedFromTag(EIOTags.Items.INSULATION_METAL, 1)),
                3200, 0.5f, recipeOutput);

        // endregion
    }

    protected void glass(FusedQuartzBlock block, SizedIngredient input, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(new ItemStackTemplate(block.asItem()), List.of(input), energy, experience, recipeOutput);
    }

    protected void glass(FusedQuartzBlock block, SizedIngredient input, @Nullable SizedIngredient inputAlt, int energy,
            float experience, RecipeOutput recipeOutput) {
        build(new ItemStackTemplate(block.asItem()), List.of(input), energy, experience, recipeOutput);
        if (inputAlt != null) {
            build(new ItemStackTemplate(block.asItem()), "alt", List.of(inputAlt), energy, experience, recipeOutput);
        }
    }

    protected void compositeGlass(FusedQuartzBlock block, String suffix, SizedIngredient inputA, SizedIngredient inputB,
            SizedIngredient inputBAlt, int energy, float experience, RecipeOutput recipeOutput) {
        build(new ItemStackTemplate(block.asItem()), suffix, List.of(inputA, inputB), energy, experience, recipeOutput);
        build(new ItemStackTemplate(block.asItem()), suffix + "_alt", List.of(inputA, inputBAlt), energy, experience, recipeOutput);
    }

    protected void build(ItemStackTemplate output, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIO.id("alloy_smelting/" + output.item().getKey().identifier().getPath()), inputs,
                output, energy, experience, recipeOutput);
    }

    protected void build(ItemStackTemplate output, String suffix, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIOAPI
                .rl("alloy_smelting/" + output.item().getKey().identifier().getPath() + "_" + suffix),
                inputs, output, energy, experience, recipeOutput);
    }

    protected void build(Identifier id, List<SizedIngredient> inputs, ItemStackTemplate output, int energy,
            float experience, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, id), new AlloySmeltingRecipe(inputs, output, energy, experience), null);
    }
}
