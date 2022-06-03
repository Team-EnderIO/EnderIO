package com.enderio.machines.common.recipe;

import com.enderio.core.recipes.EnderRecipe;
import net.minecraft.world.Container;

public interface MachineRecipe<C extends Container> extends EnderRecipe<C> {
    /**
     * Get the energy cost of a machine.
     * @param container Container/context. This is the state of the container *after* inputs are taken.
     */
    int getEnergyCost(C container);
}
