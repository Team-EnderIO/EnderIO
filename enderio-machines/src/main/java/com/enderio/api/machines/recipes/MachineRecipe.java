package com.enderio.api.machines.recipes;

import com.enderio.core.recipes.EnderRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface MachineRecipe<C extends Container> extends EnderRecipe<C> {
    /**
     * Get the energy cost of a machine.
     * @param container Container/context. This is the state of the container *after* inputs are taken.
     */
    int getEnergyCost(C container);

    /**
     * Craft outputs for this recipe.
     * @return An array of item and fluid outputs.
     */
    List<OutputStack> craft(C container);

    /**
     * Get the results of this machine, for display or verification purposes only.
     * @return
     */
    List<OutputStack> getResultStacks();

    /**
     * @deprecated Replaced by {@link #craft(Container)} to support multiple outputs and output types.
     */
    @Deprecated
    @Override
    default ItemStack assemble(C container) {
        // TODO: Logging..
        return ItemStack.EMPTY;
    }

    /**
     * @deprecated Should not be used.
     */
    @Deprecated
    @Override
    default ItemStack getResultItem() {
        // TODO: Logging..
        return ItemStack.EMPTY;
    }
}
