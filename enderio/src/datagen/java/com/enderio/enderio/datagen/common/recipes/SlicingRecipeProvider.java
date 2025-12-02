package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class SlicingRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        // TODO: Tormented enderman head

        build(EIOItems.ZOMBIE_ELECTRODE.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Items.ZOMBIE_HEAD),
                        Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(EIOTags.Items.SILICON),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(EIOTags.Items.SILICON)),
                20000, recipeOutput);

        build(EIOItems.Z_LOGIC_CONTROLLER.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ZOMBIE_HEAD),
                        Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOTags.Items.SILICON),
                        Ingredient.of(Items.REDSTONE), Ingredient.of(EIOTags.Items.SILICON)),
                20000, recipeOutput);

        build(EIOItems.SKELETAL_CONTRACTOR.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.SKELETON_SKULL),
                        Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ROTTEN_FLESH),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Items.ROTTEN_FLESH)),
                20000, recipeOutput);

        build(EIOItems.GUARDIAN_DIODE.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
                        Ingredient.of(EIOTags.Items.DUSTS_PRISMARINE),
                        Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Tags.Items.GEMS_PRISMARINE),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Tags.Items.GEMS_PRISMARINE)),
                20000, recipeOutput);

        build(EIOItems.ENDER_RESONATOR.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOBlocks.ENDERMAN_HEAD),
                        Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), // TODO EnderSkull
                        Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(EIOItems.VIBRANT_ALLOY_INGOT.get()),
                        Ingredient.of(EIOTags.Items.SILICON)),
                20000, recipeOutput);

    }

    protected void build(Item output, List<Ingredient> inputs, int energy, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("slicing/" + BuiltInRegistries.ITEM.getKey(output).getPath()),
                new SlicingRecipe(new ItemStack(output), inputs, energy), null);
    }

}
