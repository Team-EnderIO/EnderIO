package com.enderio.enderio.foundation.crafting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

/**
 * This context will only be used on the server.
 * @param <T>
 * @param <U>
 */
public abstract class MachineCraftingContext<T extends Recipe<U>, U extends RecipeInput> {
    // TODO: When implementing this, consider caching recipeInput until inventory contents change (lazy)
    public abstract U recipeInput();

    @Nullable
    public abstract ServerLevel level();

    public abstract int getCraftingTicks(RecipeHolder<T> recipe);

    /**
     * Called each tick, and should attempt to consume any running-costs for the tick (i.e. energy).
     *
     * @param recipe
     * @return whether the requisites for the tick have been met and consumed.
     */
    public abstract boolean tryProgressCraft(T recipe);

    // Note: while consume and insert *could* be merged - order is important so lets take the ability for us to get it wrong out of the equation.

    public boolean tryCompleteCraft(T recipe, RandomSource random) {
        U input = recipeInput();

        try (Transaction transaction = Transaction.openRoot()) {
            if (insertRecipeOutputs(recipe, input, random, transaction) && consumeRecipeInputs(recipe, input, transaction)) {
                transaction.commit();
                return true;
            }

            return false;
        }
    }

    protected abstract boolean consumeRecipeInputs(T recipe, U recipeInput, TransactionContext transaction);

    protected abstract boolean insertRecipeOutputs(T recipe, U recipeInput, RandomSource random, TransactionContext transaction);
}
