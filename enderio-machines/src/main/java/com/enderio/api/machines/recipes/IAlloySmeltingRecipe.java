package com.enderio.api.machines.recipes;

import com.enderio.api.recipe.CountedIngredient;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.world.Container;

import java.util.List;

// TODO: Implement vanilla smelting using this interface..
//       Might need a custom Container with consumedIngredientCount?
public interface IAlloySmeltingRecipe extends MachineRecipe<Container> {
    List<CountedIngredient> getInputs();
    float getExperience();
}
