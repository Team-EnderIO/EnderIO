package com.enderio.enderio.foundation.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

/**
 * The MachineCraftingManager tracks and maintains information about the current crafting recipe being processed by a machine.
 * All implementation details surrounding machine resource consumption, input consumption, and output insertion are handled by the {@link MachineCraftingContext}.
 * @param <T> The recipe type.
 * @param <U> The recipe input type.
 */
public final class MachineCraftingManager<T extends Recipe<U>, U extends RecipeInput> implements ValueIOSerializable {

    private final MachineCraftingContext<T, U> context;

    private final RandomSource randomSource;

    @Nullable
    private ResourceKey<Recipe<?>> currentRecipeId;
    private int totalCraftingTicks;
    private int craftingTicks;
    private long randomSeed;

    @Nullable
    private RecipeHolder<T> currentRecipeHolder;

    public MachineCraftingManager(MachineCraftingContext<T, U> context) {
        this.context = context;

        // TODO: New seed each time we pick up a new recipe.
        this.randomSeed = RandomSupport.generateUniqueSeed();
        this.randomSource = RandomSource.create(randomSeed);
    }

    public MachineCraftingState state() {
        if (currentRecipeId == null) {
            return MachineCraftingState.IDLE;
        }

        if (craftingTicks < totalCraftingTicks) {
            return MachineCraftingState.CRAFTING;
        }

        // TODO: May want a flag to indicate this is definite.
        // But in theory, we shouldn't ever hit this because the moment craftingTicks exceeds total it should dump outputs or get blocked.
        return MachineCraftingState.OUTPUT_BLOCKED;
    }

    public float craftingProgress() {
        if (currentRecipeId == null || totalCraftingTicks <= 0) {
            return 0;
        }

        return (float) craftingTicks / totalCraftingTicks;
    }

    public void tick() {
        ensureRecipeReady();

        if (currentRecipeId == null) {
            return;
        }

        // If we're not done yet, attempt to make progress
        if (craftingTicks < totalCraftingTicks) {
            // TODO: Do we want to do this? It just means less boilerplate in all the implementations if they're provided transactions.
            try (var transaction = Transaction.openRoot()) {
                if (context.tryProgressCraft(transaction)) {
                    craftingTicks++;
                    transaction.commit();
                }
            }
        }

        // If the recipe has finished crafting, attempt to finalize it.
        if (craftingTicks < totalCraftingTicks) {
            return;
        }

        boolean isFinished = tryFinaliseCraft();
        if (isFinished) {
            // Make sure we're ready to go again next tick.
            // This is intended to prevent 'state stutter'
            ensureRecipeReady();
        }
    }

    private void ensureRecipeReady() {
        // If everything is great, short circuit
        if (currentRecipeHolder != null && currentRecipeHolder.id().equals(currentRecipeId)) {
            return;
        }

        // Ensure current recipe is valid
        if (currentRecipeId != null) {
            if (currentRecipeHolder != null) {
                return;
            }

            // Fetch the recipe holder and store it.
            // This will reset the recipe progress if it has changed, or clear the crafting state if the recipe no longer exists.
            context.findRecipe().ifPresentOrElse(this::setRecipe, this::clearRecipe);
            return;
        }

        // Try and find a recipe we can start
        // TODO: may need to set some kind of flag to say we've searched already - sleep until notified of change.
        context.findRecipe().ifPresent(this::setRecipe);
    }

    private boolean tryFinaliseCraft() {
        try (Transaction transaction = Transaction.openRoot()) {
            if (!context.consumeRecipeInputs(currentRecipeHolder.value(), transaction)) {
                // Shouldn't really hit this, however if we do - it means we can't consume input, we'll just 'get stuck'
                return false;
            }

            // Ensure the seed is correct before attempting to insert outputs
            // This is important to ensure any recipes which have randomness will always output the same things each attempt.
            randomSource.setSeed(randomSeed);

            if (context.insertRecipeOutputs(currentRecipeHolder.value(), randomSource, transaction)) {
                transaction.commit();

                // We're done, releaase the recipe.
                clearRecipe();
                return true;
            }
        }

        return false;
    }

    private void setRecipe(RecipeHolder<T> recipe) {
        currentRecipeHolder = recipe;

        // Special case for when we're just calling setRecipe on load.
        // Will continue to reset the crafting state if the recipe has changed.
        if (recipe.id() == currentRecipeId) {
            return;
        }

        currentRecipeId = recipe.id();
        totalCraftingTicks = context.getCraftingTicks(recipe);
        craftingTicks = 0;
        randomSeed = RandomSupport.generateUniqueSeed();
    }

    private void clearRecipe() {
        currentRecipeId = null;
        currentRecipeHolder = null;
    }

    @Override
    public void serialize(ValueOutput output) {
        if (currentRecipeId != null) {
            output.store("CurrentRecipeId", ResourceKey.codec(Registries.RECIPE), currentRecipeId);
            output.putInt("CraftingTicks", craftingTicks);
            output.putLong("RandomSeed", randomSeed);
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read("CurrentRecipeId", ResourceKey.codec(Registries.RECIPE)).ifPresent(id -> currentRecipeId = id);

        if (currentRecipeId != null) {
            craftingTicks = input.getIntOr("CraftingTicks", 0);
            randomSeed = input.getLongOr("RandomSeed", RandomSupport.generateUniqueSeed());
        }
    }
}
