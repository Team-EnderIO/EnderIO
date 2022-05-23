package com.enderio.machines.common.integration.jei.helpers;

import com.enderio.base.common.recipe.EnderIngredient;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class AlloySmelterFurnaceRecipe extends AlloySmeltingRecipe {
    public AlloySmelterFurnaceRecipe(SmeltingRecipe recipe) {
        super(recipe.getId(), recipe.getIngredients().stream().map(ingredient -> new EnderIngredient(ingredient, 1)).toList(), recipe.getResultItem(), AlloySmelterBlockEntity.RF_PER_ITEM, recipe.getExperience());
    }
}