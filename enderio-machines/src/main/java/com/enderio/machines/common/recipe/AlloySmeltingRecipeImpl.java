package com.enderio.machines.common.recipe;

import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class AlloySmeltingRecipeImpl extends AlloySmeltingRecipe {
    public AlloySmeltingRecipeImpl(ResourceLocation id, List<CountedIngredient> inputs, ItemStack result, int energy, float experience) {
        super(id, inputs, result, energy, experience);
    }

    @Override
    public DataGenSerializer<AlloySmeltingRecipe, Container> getSerializer() {
        return MachineRecipes.Serializer.ALLOY_SMELTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ALLOY_SMELTING;
    }
}
