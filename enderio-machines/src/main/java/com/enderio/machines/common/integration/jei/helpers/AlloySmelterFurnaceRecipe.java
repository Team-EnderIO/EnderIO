package com.enderio.machines.common.integration.jei.helpers;

import com.enderio.api.recipe.EnderIngredient;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class AlloySmelterFurnaceRecipe extends AlloySmeltingRecipeImpl {
    public AlloySmelterFurnaceRecipe(SmeltingRecipe recipe) {
        super(recipe.getId(), recipe.getIngredients().stream().map(ingredient -> new EnderIngredient(ingredient, 1)).toList(), recipe.getResultItem(), AlloySmelterBlockEntity.RF_PER_ITEM, recipe.getExperience());
    }
}