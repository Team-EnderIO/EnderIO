package com.enderio.endergy.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.endergy.common.init.EndergyItems;
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
import net.minecraft.data.recipes.RecipeOutput;
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

import java.util.List;
import java.util.Map;

public class AlloyRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        // TODO: Review all recipes and alloy compositions
        // TODO: Experience values need set properly, i just used a filler value off the
        // top of my head

        // region Metal Alloys

        build(new ItemStack(EndergyItems.CRUDE_STEEL_INGOT.get()),
                List.of(SizedIngredient.of(Tags.Items.GRAVELS, 1),
                    SizedIngredient.of(Items.CLAY_BALL, 1),
                    SizedIngredient.of(Tags.Items.COBBLESTONES_NORMAL, 1)),
                5000, 0.3f, recipeOutput);

        build(new ItemStack(EndergyItems.CRYSTALLINE_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY, 1),
                    SizedIngredient.of(Tags.Items.INGOTS_GOLD, 1)),
                10000, 0.3f, recipeOutput);

        build(new ItemStack(EndergyItems.MELODIC_ALLOY_INGOT.get()),
                List.of(SizedIngredient.of(Items.POPPED_CHORUS_FRUIT, 1),
                    SizedIngredient.of(EIOTags.Items.INGOTS_END_STEEL, 1)),
                20000, 0.3f, recipeOutput);

        build(new ItemStack(EndergyItems.STELLAR_ALLOY_INGOT.get(), 2),
                List.of(SizedIngredient.of(Items.NETHER_STAR, 1),
                    SizedIngredient.of(EndergyItems.MELODIC_ALLOY_INGOT, 1),
                    SizedIngredient.of(Items.CLAY_BALL, 4)),
                20000, 0.3f, recipeOutput);

        // TODO: No recipe for crystalline pink slime or energetic silver?

        // TODO: How to replace silver-based alloys.
//        build(new ItemStack(EndergyItems.VIVID_ALLOY_INGOT.get()),
//            List.of(SizedIngredient.of(Tags.Items.ENDER_PEARLS, 1),
//                SizedIngredient.of(EndergyItems.ENERGETIC_SILVER_INGOT, 1)),
//            10000, 0.3f, recipeOutput);


        // endregion
    }

    protected void build(ItemStack output, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIO.rl("alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath()), inputs,
                output, energy, experience, recipeOutput);
    }

    protected void build(ItemStack output, String suffix, List<SizedIngredient> inputs, int energy, float experience,
            RecipeOutput recipeOutput) {
        build(EnderIOAPI
                .rl("alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath() + "_" + suffix),
                inputs, output, energy, experience, recipeOutput);
    }

    protected void build(ResourceLocation id, List<SizedIngredient> inputs, ItemStack output, int energy,
            float experience, RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new AlloySmeltingRecipe(inputs, output, energy, experience), null);
    }

}
