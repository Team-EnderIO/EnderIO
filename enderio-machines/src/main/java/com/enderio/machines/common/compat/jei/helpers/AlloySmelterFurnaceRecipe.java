package com.enderio.machines.common.compat.jei.helpers;

import com.enderio.api.recipe.CountedIngredient;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class AlloySmelterFurnaceRecipe extends AlloySmeltingRecipeImpl {
    public AlloySmelterFurnaceRecipe(SmeltingRecipe recipe) {
        super(recipe.getId(), recipe.getIngredients().stream().map(ingredient -> new CountedIngredient(ingredient, 1)).toList(), recipe.getResultItem(), AlloySmelterBlockEntity.RF_PER_ITEM, recipe.getExperience());
    }
}