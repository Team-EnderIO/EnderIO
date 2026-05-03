package com.enderio.enderio.foundation.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * The MachineCraftingManager tracks and maintains information about the current crafting recipe being processed by a machine.
 * All implementation details surrounding machine resource consumption, input consumption, and output insertion are handled by the {@link MachineCraftingContext}.
 * @param <T> The recipe type.
 * @param <U> The recipe input type.
 */
public final class MachineCraftingManager<T extends Recipe<U>, U extends RecipeInput> implements ValueIOSerializable {

    private final RecipeType<T> recipeType;
    private final MachineCraftingContext<T, U> context;

    private final RandomSource randomSource;

    @Nullable
    private ResourceKey<Recipe<?>> currentRecipeId;
    private int totalCraftingTicks;
    private int craftingTicks;
    private long randomSeed;

    @Nullable
    private RecipeHolder<T> currentRecipeHolder;

    @Nullable
    private U lastRecipeInput;

    private WeakReference<@Nullable RecipeManager> cachedRecipeManager = new WeakReference<>(null);
    private boolean hasInitiallyCachedRecipeManager;

    public MachineCraftingManager(RecipeType<T> recipeType, MachineCraftingContext<T, U> context) {
        this.recipeType = recipeType;
        this.context = context;
        this.randomSeed = RandomSupport.generateUniqueSeed();
        this.randomSource = RandomSource.create(randomSeed);
    }

    public MachineCraftingStatus status() {
        if (currentRecipeId == null) {
            return MachineCraftingStatus.IDLE;
        }

        if (craftingTicks < totalCraftingTicks) {
            return MachineCraftingStatus.ACTIVE;
        }

        // TODO: May want a flag to indicate this is definite.
        // But in theory, we shouldn't ever hit this because the moment craftingTicks exceeds total it should dump outputs or get blocked.
        return MachineCraftingStatus.OUTPUT_BLOCKED;
    }

    public float craftingProgress() {
        if (currentRecipeId == null || totalCraftingTicks <= 0) {
            return 0;
        }

        return (float) craftingTicks / totalCraftingTicks;
    }

    @Nullable
    public RecipeHolder<T> currentRecipe() {
        return currentRecipeHolder;
    }

    /**
     * We should tick if:
     * - we actively have a recipe
     * - we are looking for a new recipe because recipe input has changed (or we've not yet checked - i.e. newly placed machine)
     * - the recipe manager has changed since last tick, indicating recipes may have been changed.
     * @return whether the manager should tick.
     */
    private boolean shouldTick() {
        var level = context.level();
        if (level == null) {
            return false;
        }

        // If this is the first time we've accessed the recipe manager, ensure it is tracked.
        var recipeManager = level.recipeAccess();
        if (!hasInitiallyCachedRecipeManager) {
            cachedRecipeManager = new WeakReference<>(recipeManager);
            hasInitiallyCachedRecipeManager = true;
        }

        // The recipe input has changed.
        var recipeInput = context.recipeInput();
        if (lastRecipeInput == null || !lastRecipeInput.equals(recipeInput)) {
            lastRecipeInput = recipeInput;
            return true;
        }

        if (recipeManager != cachedRecipeManager.get()) {
            // Track the new recipe manager *and* force us to refresh the current recipe holder
            cachedRecipeManager = new WeakReference<>(recipeManager);
            currentRecipeHolder = null;
            return true;
        }

        // Only tick if we have a recipe to craft
        return currentRecipeId != null;
    }

    public void tick() {
        if (!shouldTick()) {
            return;
        }

        // Ensure we have a recipe, bail if we can't.
        ensureRecipeReady();
        if (currentRecipeId == null) {
            return;
        }

        // If we're not done yet, attempt to make progress
        if (craftingTicks < totalCraftingTicks) {
            if (context.tryProgressCraft(currentRecipeHolder.value())) {
                craftingTicks++;
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
        ServerLevel level = Objects.requireNonNull(context.level());
        U recipeInput = context.recipeInput();
        RecipeManager recipeManager = level.recipeAccess();

        // Ensure current recipe is valid
        if (currentRecipeId != null) {
            if (currentRecipeHolder != null) {
                if (currentRecipeHolder.value().matches(recipeInput, level)) {
                    return;
                }
            }

            // Fetch the recipe and store it.
            // This will reset the recipe progress if it has changed, or clear the crafting state if the recipe no longer exists.
            recipeManager.getRecipeFor(recipeType, recipeInput, level, currentRecipeId).ifPresentOrElse(this::setRecipe, this::clearRecipe);
            return;
        }

        // Try and find a recipe we can start, if we get one set it and roll the random seed
        recipeManager.getRecipeFor(recipeType, recipeInput, level).ifPresent(newRecipe -> {
            setRecipe(newRecipe);
            this.randomSeed = RandomSupport.generateUniqueSeed();
        });
    }

    private boolean tryFinaliseCraft() {
        Objects.requireNonNull(currentRecipeHolder);

        try (Transaction transaction = Transaction.openRoot()) {
            // Ensure the seed is correct before attempting to insert outputs
            // This is important to ensure any recipes which have randomness will always output the same things each attempt.
            randomSource.setSeed(randomSeed);

            // Try and insert result first, and *then* consume inputs. This ensures that the recipeInput is still valid for the context to use.
            if (!context.insertRecipeOutputs(currentRecipeHolder.value(), randomSource, transaction)) {
                return false;
            }

            // Now try to consume inputs - shouldn't fail, but if it does we'll just 'get stuck'
            if (context.consumeRecipeInputs(currentRecipeHolder.value(), transaction)) {
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

    @Nullable
    public MachineCraftingState getCraftingState() {
        if (currentRecipeId == null) {
            return null;
        }

        return new MachineCraftingState(currentRecipeId, craftingTicks, randomSeed);
    }

    public void applyCraftingState(@Nullable MachineCraftingState craftingState) {
        if (craftingState == null) {
            return;
        }

        currentRecipeId = craftingState.recipeId();
        craftingTicks = craftingState.craftingTicks();
        randomSeed = craftingState.randomSeed();
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
