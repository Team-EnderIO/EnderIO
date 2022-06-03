package com.enderio.machines.common.recipe;

import com.enderio.core.recipes.EnderRecipe;
import net.minecraft.world.Container;

public interface MachineRecipe<C extends Container> extends EnderRecipe<C> {
    int getEnergyCost(C container);
}
