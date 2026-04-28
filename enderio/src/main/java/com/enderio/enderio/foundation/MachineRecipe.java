package com.enderio.enderio.foundation;

import com.enderio.core.common.recipes.OutputStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public interface MachineRecipe<T extends RecipeInput> extends Recipe<T> {
    /**
     * @param input the recipe input
     * @return the operation time in ticks.
     */
    int getOperationTime(T input);

    /**
     * Craft outputs for this recipe.
     * @return An array of item and fluid outputs.
     */
    List<OutputStack> craft(T container, RegistryAccess registryAccess);

    /**
     * Craft outputs for this recipe.
     * @return An array of item and fluid outputs.
     */
    default List<OutputStack> craft(T container, RegistryAccess registryAccess, RandomSource random) {
        return craft(container, registryAccess);
    }

    /**
     * Get the results of this machine, for display or verification purposes only.
     * @return
     */
    List<OutputStack> getResultStacks(RegistryAccess registryAccess);

    /**
     * @deprecated Replaced by {@link #craft(T, RegistryAccess)} to support multiple outputs and output types.
     */
    @Deprecated
    @Override
    default ItemStack assemble(T container) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default String group() {
        return "";
    }
}
