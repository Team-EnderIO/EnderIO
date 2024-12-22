package com.enderio.machines.data.recipes;

import com.enderio.EnderIOBase;
import com.enderio.base.common.block.glass.FusedQuartzBlock;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassIdentifier;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

public class AlloyRecipeProvider extends RecipeProvider {

    public AlloyRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // TODO: Review all recipes and alloy compositions
        // TODO: Experience values need set properly, i just used a filler value off the
        // top of my head

        // region Metal Alloys

        build(new ItemStack(EIOItems.ENERGETIC_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(Tags.Items.DUSTS_REDSTONE, 1), SizedIngredient.of(Tags.Items.INGOTS_GOLD, 1),
                        SizedIngredient.of(Tags.Items.DUSTS_GLOWSTONE, 1)),
                4800, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.COPPER_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(Tags.Items.INGOTS_COPPER, 1), SizedIngredient.of(EIOTags.Items.SILICON, 1)),
                3200, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.VIBRANT_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY, 1),
                        SizedIngredient.of(Tags.Items.ENDER_PEARLS, 1)),
                4800, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.REDSTONE_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(Tags.Items.DUSTS_REDSTONE, 1), SizedIngredient.of(EIOTags.Items.SILICON, 1)),
                3200, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.CONDUCTIVE_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(EIOTags.Items.INGOTS_COPPER_ALLOY, 1),
                        SizedIngredient.of(Tags.Items.INGOTS_IRON, 1),
                        SizedIngredient.of(Tags.Items.DUSTS_REDSTONE, 1)),
                4800, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.PULSATING_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(Tags.Items.INGOTS_IRON, 1), SizedIngredient.of(Tags.Items.ENDER_PEARLS, 1)),
                4800, 0.3f, recipeOutput);
        build(EnderIOBase.loc("dark_steel_ingot_with_coal"),
                List.of(SizedIngredient.of(Tags.Items.INGOTS_IRON, 1), SizedIngredient.of(Items.COAL, 2),
                        SizedIngredient.of(Tags.Items.OBSIDIANS, 1)),
                new ItemStack(EIOItems.DARK_STEEL_INGOT.get()), 6400, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.DARK_STEEL_INGOT.get()), List.of(SizedIngredient.of(Tags.Items.INGOTS_IRON, 1),
                SizedIngredient.of(EIOTags.Items.DUSTS_COAL, 1), SizedIngredient.of(Tags.Items.OBSIDIANS, 1)), 6400,
                0.3f, recipeOutput);
        build(new ItemStack(EIOItems.SOULARIUM_INGOT.get()),
                List.of(new SizedIngredient(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL), 1),
                        SizedIngredient.of(Tags.Items.INGOTS_GOLD, 1)),
                5600, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.END_STEEL_INGOT.get()), List.of(SizedIngredient.of(Tags.Items.END_STONES, 1),
                SizedIngredient.of(EIOTags.Items.INGOTS_DARK_STEEL, 1), SizedIngredient.of(Tags.Items.OBSIDIANS, 1)),
                6400, 0.3f, recipeOutput);

        // endregion

        // TODO: Balance below energies:

        // region Dyes

        build(new ItemStack(EIOItems.DYE_GREEN.get()), "clippings",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_GREEN.get(), 6), SizedIngredient.of(Items.EGG, 1)),
                1000, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.DYE_GREEN.get(), 2), "double_clippings",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_GREEN.get(), 12),
                        SizedIngredient.of(Tags.Items.SLIMEBALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStack(EIOItems.DYE_BROWN.get()), "twigs",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_BROWN.get(), 6), SizedIngredient.of(Items.EGG, 1)),
                1000, 0.3f, recipeOutput);
        build(new ItemStack(EIOItems.DYE_BROWN.get(), 2), "twigs_double",
                List.of(SizedIngredient.of(EIOItems.PLANT_MATTER_BROWN.get(), 12),
                        SizedIngredient.of(Tags.Items.SLIMEBALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStack(EIOItems.DYE_BLACK.get()),
                List.of(SizedIngredient.of(EIOTags.Items.DUSTS_COAL, 1), SizedIngredient.of(Items.EGG, 1)), 1000, 0.3f,
                recipeOutput);
        build(new ItemStack(EIOItems.DYE_BLACK.get(), 2), "double",
                List.of(SizedIngredient.of(EIOTags.Items.DUSTS_COAL, 2), SizedIngredient.of(Tags.Items.SLIMEBALLS, 1)),
                1600, 0.3f, recipeOutput);

        build(new ItemStack(Items.RED_DYE, 12), List.of(SizedIngredient.of(Items.BEETROOT, 1),
                SizedIngredient.of(Items.CLAY_BALL, 3), SizedIngredient.of(Items.EGG, 6)), 1600, 0.3f, recipeOutput);

        // endregion

        // region Glass

        for (Map.Entry<GlassIdentifier, GlassBlocks> glassGroup : EIOBlocks.GLASS_BLOCKS.entrySet()) {
            GlassIdentifier identifier = glassGroup.getKey();
            if (identifier.collisionPredicate() == GlassCollisionPredicate.NONE) {
                FusedQuartzBlock clear = glassGroup.getValue().CLEAR.get();
                var mainIngredient = identifier.explosion_resistance() ? SizedIngredient.of(Tags.Items.GEMS_QUARTZ, 4)
                        : SizedIngredient.of(Tags.Items.GLASS_BLOCKS_COLORLESS, 1);
                @Nullable
                var altIngredient = identifier.explosion_resistance()
                        ? SizedIngredient.of(EIOTags.Items.STORAGE_BLOCKS_QUARTZ, 1)
                        : null;
                var energy = identifier.explosion_resistance() ? 6400 : 3200;
                if (identifier.lighting() == GlassLighting.NONE) {
                    glass(clear, mainIngredient, altIngredient, energy, 0.3f, recipeOutput);
                } else {
                    var composite = identifier.lighting() == GlassLighting.EMITTING
                            ? SizedIngredient.of(Tags.Items.DUSTS_GLOWSTONE, 4)
                            : SizedIngredient.of(Tags.Items.GEMS_AMETHYST, 4);
                    var compositeB = identifier.lighting() == GlassLighting.EMITTING
                            ? SizedIngredient.of(Blocks.GLOWSTONE, 1)
                            : SizedIngredient.of(EIOTags.Items.STORAGE_BLOCKS_AMETHYST, 1);
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

        build(new ItemStack(EIOItems.NETHERCOTTA.get()), List.of(SizedIngredient.of(Tags.Items.BRICKS_NETHER, 1),
                SizedIngredient.of(Items.NETHER_WART, 4), SizedIngredient.of(Items.CLAY_BALL, 6)), 7600, 0.3f,
                recipeOutput);

        build(new ItemStack(EIOItems.CAKE_BASE.get(), 2),
                List.of(SizedIngredient.of(EIOItems.FLOUR.get(), 3), SizedIngredient.of(Items.EGG, 1)), 2000, 0.3f,
                recipeOutput);

        build(new ItemStack(Items.COOKIE, 8),
                List.of(SizedIngredient.of(EIOItems.FLOUR.get(), 1), SizedIngredient.of(Items.COCOA_BEANS, 1)), 2000,
                0.3f, recipeOutput);

        build(new ItemStack(EIOItems.PHOTOVOLTAIC_PLATE.get()),
                List.of(SizedIngredient.of(EIOItems.PHOTOVOLTAIC_COMPOSITE.get(), 3)), 5600, 0.3f, recipeOutput);

        build(new ItemStack(Items.ENDER_PEARL), List.of(SizedIngredient.of(EIOTags.Items.DUSTS_ENDER, 9)), 2000, 0.3f,
                recipeOutput);

//        build(new ItemStack(), List.of(EnderIngredient.of(EIOItems.GRAINS_OF_INFINITY.get()), EnderIngredient.of(EIOTags.Items.DUSTS_COAL)), 5000, 0.3f, recipeOutput);

        build(new ItemStack(Items.DEAD_BUSH), List.of(SizedIngredient.of(ItemTags.SAPLINGS, 1)), 500, 0.3f,
                recipeOutput);

//        build(new ItemStack(EIOItems.DARK_STEEL_UPGRADE_BLANK.get()), List.of(SizedIngredient.of(EIOBlocks.DARK_STEEL_BARS.get()), SizedIngredient.of(Items.CLAY_BALL), SizedIngredient.of(4, Tags.Items.STRING)), 30000, 0.3f, recipeOutput);

        build(new ItemStack(EIOItems.CLAYED_GLOWSTONE.get(), 2),
                List.of(SizedIngredient.of(Tags.Items.DUSTS_GLOWSTONE, 1), SizedIngredient.of(Items.CLAY_BALL, 1)),
                3200, 0.3f, recipeOutput);

        build(new ItemStack(EIOBlocks.INDUSTRIAL_INSULATION.get()),
                List.of(SizedIngredient.of(EIOTags.Items.DUSTS_LAPIS, 1), SizedIngredient.of(ItemTags.WOOL, 1),
                        SizedIngredient.of(EIOTags.Items.INSULATION_METAL, 1)),
                3200, 0.5f, recipeOutput);

        // endregion
    }

    protected void glass(FusedQuartzBlock block, SizedIngredient input, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(new ItemStack(block), List.of(input), energy, experience, recipeOutput);
    }

    protected void glass(FusedQuartzBlock block, SizedIngredient input, @Nullable SizedIngredient inputAlt, int energy,
            float experience, RecipeOutput recipeOutput) {
        build(new ItemStack(block), List.of(input), energy, experience, recipeOutput);
        if (inputAlt != null) {
            build(new ItemStack(block), "alt", List.of(inputAlt), energy, experience, recipeOutput);
        }
    }

    protected void compositeGlass(FusedQuartzBlock block, String suffix, SizedIngredient inputA, SizedIngredient inputB,
            SizedIngredient inputBAlt, int energy, float experience, RecipeOutput recipeOutput) {
        build(new ItemStack(block), suffix, List.of(inputA, inputB), energy, experience, recipeOutput);
        build(new ItemStack(block), suffix + "_alt", List.of(inputA, inputBAlt), energy, experience, recipeOutput);
    }

    protected void build(ItemStack output, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIOBase.loc("alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath()), inputs,
                output, energy, experience, recipeOutput);
    }

    protected void build(ItemStack output, String suffix, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIOBase
                .loc("alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath() + "_" + suffix),
                inputs, output, energy, experience, recipeOutput);
    }

    protected void build(ResourceLocation id, List<SizedIngredient> inputs, ItemStack output, int energy,
            float experience, RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new AlloySmeltingRecipe(inputs, output, energy, experience), null);
    }

}
