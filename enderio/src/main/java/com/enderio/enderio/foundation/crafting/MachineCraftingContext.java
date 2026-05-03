package com.enderio.enderio.foundation.crafting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

/**
 * This context will only be used on the server.
 * @param <T>
 * @param <U>
 */
public interface MachineCraftingContext<T extends Recipe<U>, U extends RecipeInput> {
    // TODO: When implementing this, consider caching recipeInput until inventory contents change (lazy)
    U recipeInput();

    @Nullable
    ServerLevel level();

    int getCraftingTicks(RecipeHolder<T> recipe);

    /**
     * Called each tick, and should attempt to consume any running-costs for the tick (i.e. energy).
     *
     * @param recipe
     * @return whether the requisites for the tick have been met and consumed.
     */
    boolean tryProgressCraft(T recipe);

    // Note: while consume and insert *could* be merged - order is important so lets take the ability for us to get it wrong out of the equation.

    boolean consumeRecipeInputs(T recipe, TransactionContext transaction);

    boolean insertRecipeOutputs(T recipe, RandomSource random, TransactionContext transaction);
}
