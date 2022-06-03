package com.enderio.api.machines.recipes;

import com.enderio.api.recipe.CountedIngredient;
import com.enderio.machines.common.blockentity.NewAlloySmelterBlockEntity;
import com.enderio.machines.common.recipe.MachineRecipe;

import java.util.List;

// TODO: Implement vanilla smelting using this interface..
//       Might need a custom Container with consumedIngredientCount?
public interface IAlloySmeltingRecipe extends MachineRecipe<NewAlloySmelterBlockEntity.AlloySmelterContainer> {
    /**
     * Get the inputs for the alloy smelting recipe.
     */
    List<CountedIngredient> getInputs();
    float getExperience();
}
