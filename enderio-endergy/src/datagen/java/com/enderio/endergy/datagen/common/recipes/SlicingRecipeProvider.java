package com.enderio.endergy.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.endergy.common.init.EndergyItems;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class SlicingRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        build(EndergyItems.TOTEMIC_CAPACITOR.get(),
                List.of(Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.TOTEM_OF_UNDYING),
                        Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY),
                        Ingredient.of(EndergyItems.CRYSTALLINE_CAPACITOR.get()), Ingredient.of(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY)),
                20000, recipeOutput);
    }

    protected void build(Item output, List<Ingredient> inputs, int energy, RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("slicing/" + BuiltInRegistries.ITEM.getKey(output).getPath()),
                new SlicingRecipe(new ItemStack(output), inputs, energy), null);
    }

}
