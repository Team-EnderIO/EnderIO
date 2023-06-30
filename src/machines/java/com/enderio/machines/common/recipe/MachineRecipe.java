package com.enderio.machines.common.recipe;

import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.core.common.recipes.OutputStack;
import net.minecraft.core.RegistryAccess;
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
    List<OutputStack> craft(C container, RegistryAccess registryAccess);

    /**
     * Get the results of this machine, for display or verification purposes only.
     * @return
     */
    List<OutputStack> getResultStacks(RegistryAccess registryAccess);

    /**
     * @deprecated Replaced by {@link #craft(Container)} to support multiple outputs and output types.
     */
    @Deprecated
    @Override
    default ItemStack assemble(C container, RegistryAccess registryAccess) {
        // TODO: Logging..
        return ItemStack.EMPTY;
    }

    /**
     * @deprecated Should not be used.
     */
    @Deprecated
    @Override
    default ItemStack getResultItem(RegistryAccess registryAccess) {
        // TODO: Logging..
        return ItemStack.EMPTY;
    }
}
