package com.enderio.enderio.foundation.task;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.storage.EnderResourceHandler;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.state.MachineState;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// TODO: A recipe interface that doesn't require power :)
public abstract class CraftingMachineTask<R extends MachineRecipe<T>, T extends RecipeInput> implements MachineTask {

    protected final Level level;
    protected final MachineInventory inventory;
    @Nullable
    protected final EnderResourceHandler<FluidResource> fluidStorage;
    @Nullable
    protected final MultiSlotAccess outputSlots;
    protected final T recipeInput;

    @Nullable
    private ResourceKey<Recipe<?>> recipeId;

    @Nullable
    private RecipeHolder<R> recipeHolder;

    private int progressMade;
    private int progressRequired;

    private boolean hasConsumedInputs;
    private boolean hasDeterminedOutputs;

    private List<OutputStack> outputs = List.of();

    private boolean isComplete;

    private static final Logger LOGGER = LogUtils.getLogger();

    public CraftingMachineTask(Level level, MachineInventory inventory, T recipeInput,
            @Nullable MultiSlotAccess outputSlots, @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, null, recipeInput, outputSlots, recipe);
    }

    public CraftingMachineTask(Level level, MachineInventory inventory,
                               @Nullable EnderResourceHandler<FluidResource> fluidStorage, T recipeInput, @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, fluidStorage, recipeInput, null, recipe);
    }

    public CraftingMachineTask(Level level, MachineInventory inventory,
                               @Nullable EnderResourceHandler<FluidResource> fluidStorage, T recipeInput, @Nullable MultiSlotAccess outputSlots,
                               @Nullable RecipeHolder<R> recipe) {
        this.level = level;
        this.inventory = inventory;
        this.fluidStorage = fluidStorage;
        this.recipeInput = recipeInput;
        this.outputSlots = outputSlots;
        this.recipeId = recipe == null ? null : recipe.id();
        this.recipeHolder = recipe;
        inventory.updateMachineState(MachineState.FULL_OUTPUT, false);
        inventory.updateMachineState(MachineState.EMPTY_INPUT, true);
    }

    public MachineInventory getInventory() {
        return inventory;
    }

    @Nullable
    public RecipeHolder<R> getRecipeHolder() {
        return recipeHolder;
    }

    // TODO: NEO-PORT: Should this return the holder?
    @Nullable
    public R getRecipe() {
        if (recipeHolder == null) {
            return null;
        }

        return recipeHolder.value();
    }

    // region Abstract Implementation

    protected abstract void consumeInputs(R recipe);

    protected abstract int makeProgress(int remainingProgress);

    protected abstract int getProgressRequired(R recipe);

    // endregion

    // region Overridable Events

    /**
     * This is fired right before recipe outputs are determined for the task.
     */
    protected T prepareToDetermineOutputs(R recipe, T recipeInput) {
        return recipeInput;
    }

    // endregion

    // region Task Implementation

    @Override
    public void tick() {
        // If the recipe is done, don't let it tick.
        if (isComplete) {
            return;
        }

        // Cancel if we somehow have no recipe.
        if (recipeId == null) {
            isComplete = true;
            return;
        }

        if (recipeHolder == null) {
            // TODO: Temp.
            recipeHolder = loadRecipe(recipeId.identifier());

            // If we can't find the recipe, abort.
            if (recipeHolder == null) {
                isComplete = true;
                return;
            }
        }

        // Get the outputs list.
        if (!hasDeterminedOutputs) {
            hasDeterminedOutputs = true;
            T processedRecipeInput = prepareToDetermineOutputs(recipeHolder.value(), recipeInput);
            outputs = recipeHolder.value().craft(processedRecipeInput, level.registryAccess());

            // TODO: Compact any items that are the same into singular stacks?

            // Store the recipe energy cost.
            progressRequired = getProgressRequired(recipeHolder.value());
        }

        // If we don't have a recipe match, complete the task and wait for a new one.
        if (!recipeHolder.value().matches(recipeInput, level)) {
            inventory.updateMachineState(MachineState.EMPTY_INPUT, true);
            isComplete = true;
            return;
        }
        inventory.updateMachineState(MachineState.EMPTY_INPUT, false);

        // Try to consume as much energy as possible to finish the craft.
        if (progressMade < progressRequired) {
            progressMade += makeProgress(progressRequired - progressMade);
        }

        // If the recipe has been crafted, attempt to put it into storage
        if (progressMade >= progressRequired) {
            // Attempt to complete the craft
            boolean placeOutputs = placeOutputs(outputs, false);
            inventory.updateMachineState(MachineState.FULL_OUTPUT, !placeOutputs);
            if (placeOutputs) {
                // Take the inputs
                consumeInputs(recipeHolder.value());

                // The receiver was able to take the outputs, task complete.
                isComplete = true;
            }
        }
    }

    @Override
    public float getProgress() {
        if (recipeId == null) {
            return 0.0f;
        }

        return progressMade / (float) progressRequired;
    }

    @Override
    public boolean isCompleted() {
        return isComplete;
    }

    // endregion

    // region Resource Depletion

    protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
        // TODO: Handle fluids too.

        // return early if there are no output slots
        if (outputSlots == null) {
            return false;
        }

        // See that we can add all the outputs
        try (Transaction transaction = Transaction.openRoot()) {
            for (OutputStack output : outputs) {
                ItemResource item = ItemResource.of(output.getItem());

                int toInsert = output.getItem().getCount();
                for (SingleSlotAccess outputAccess : outputSlots.getAccesses()) {
                    int inserted = outputAccess.insert(getInventory(), item, toInsert, transaction);
                    toInsert -= inserted;

                    if (toInsert == 0) {
                        break;
                    }
                }

                // If we fail, say we can't accept these outputs
                if (toInsert > 0) {
                    return false;
                }
            }

            // If we've reached this point, we have placed all outputs - commit.
            transaction.commit();
            return true;
        }
    }

    // endregion

    // region Serialization

    private static final String KEY_RECIPE_ID = "RecipeId";
    private static final String KEY_PROGRESS_MADE = "ProgressMade";
    private static final String KEY_PROGRESS_REQUIRED = "ProgressRequired";
    private static final String KEY_HAS_COLLECTED_INPUTS = "HasCollectedInputs";
    private static final String KEY_IS_COMPLETE = "IsComplete";
    private static final String KEY_HAS_DETERMINED_OUTPUTS = "HasDeterminedOutputs";
    private static final String KEY_OUTPUTS = "Outputs";

    @Override
    public void serialize(ValueOutput output) {
        if (recipeId == null) {
            return;
        }

        output.store(KEY_RECIPE_ID, Recipe.KEY_CODEC, recipeId);
        output.putInt(KEY_PROGRESS_MADE, progressMade);
        output.putInt(KEY_PROGRESS_REQUIRED, progressRequired);
        output.putBoolean(KEY_HAS_COLLECTED_INPUTS, hasConsumedInputs);
        output.putBoolean(KEY_IS_COMPLETE, isComplete);

        output.putBoolean(KEY_HAS_DETERMINED_OUTPUTS, hasDeterminedOutputs);
        if (hasDeterminedOutputs) {
            var outputList = output.list(KEY_OUTPUTS, OutputStack.CODEC);
            for (OutputStack stack : outputs) {
                outputList.add(stack);
            }
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        recipeId = input.read(KEY_RECIPE_ID, Recipe.KEY_CODEC).orElse(null);
        progressMade = input.getIntOr(KEY_PROGRESS_MADE, 0);
        progressRequired = input.getIntOr(KEY_PROGRESS_REQUIRED, 0);
        hasConsumedInputs = input.getBooleanOr(KEY_HAS_COLLECTED_INPUTS, false);
        isComplete = input.getBooleanOr(KEY_IS_COMPLETE, false);

        hasDeterminedOutputs = input.getBooleanOr(KEY_HAS_DETERMINED_OUTPUTS, false);
        if (hasDeterminedOutputs) {
            var outputList = input.listOrEmpty(KEY_OUTPUTS, OutputStack.CODEC);
            outputs = new ArrayList<>();
            for (OutputStack stack : outputList) {
                outputs.add(stack);
            }
        }
    }

    @Nullable
    protected RecipeHolder<R> loadRecipe(Identifier id) {
        try {
            // noinspection unchecked
            if (level instanceof ServerLevel serverLevel) {
                return  (RecipeHolder<R>) serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,id)).orElse(null);
            }
            return null;
        } catch (ClassCastException ex) {
            // Can occur when loading a world with the old smelting recipe system.
            LOGGER.warn("Failed to cast recipe '{}' to the correct type, not loading in-progress recipe.", id);
            return null;
        }
    }

    // endregion
}
