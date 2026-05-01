package com.enderio.enderio.foundation.crafting;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Optional;

public interface MachineCraftingContext<T extends Recipe<U>, U extends RecipeInput> {
    // TODO: When implementing this, consider caching recipeInput until inventory contents change (lazy)
    U recipeInput();

    // TODO: When implementing this, consider using vanillas RecipeCache.
    Optional<RecipeHolder<T>> findRecipe();

    int getCraftingTicks(RecipeHolder<T> recipe);

    /**
     * Called each tick, and should attempt to consume any running-costs for the tick (i.e. energy).
     * @param transaction the transaction to use for any resource operations.
     * @return whether the requisites for the tick have been met and consumed.
     */
    boolean tryProgressCraft(TransactionContext transaction);

    boolean consumeRecipeInputs(T recipe, TransactionContext transaction);

    boolean insertRecipeOutputs(T recipe, RandomSource random, TransactionContext transaction);
}
