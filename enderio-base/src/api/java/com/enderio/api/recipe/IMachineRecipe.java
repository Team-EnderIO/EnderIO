package com.enderio.api.recipe;

import net.minecraft.world.Container;

/**
 * A recipe for a power-consuming machine.
 */
public interface IMachineRecipe<R extends IMachineRecipe<R, C>, C extends Container> extends IEnderRecipe<R, C> {
    /**
     * Get the overall energy cost of the recipe
     */
    int getEnergyCost();
}