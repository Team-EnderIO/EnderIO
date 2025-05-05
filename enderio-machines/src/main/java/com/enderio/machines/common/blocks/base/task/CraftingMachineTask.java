package com.enderio.machines.common.blocks.base.task;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

// TODO: A recipe interface that doesn't require power :)
public abstract class CraftingMachineTask<R extends MachineRecipe<T>, T extends RecipeInput> implements MachineTask {

    protected final Level level;
    protected final MachineInventory inventory;
    @Nullable
    protected final MachineFluidHandler fluidHandler;
    @Nullable
    protected final MultiSlotAccess outputSlots;
    protected final T recipeInput;

    @Nullable
    private RecipeHolder<R> recipe;

    private int progressMade;
    private int progressRequired;

    private boolean hasConsumedInputs;
    private boolean hasDeterminedOutputs;

    private List<OutputStack> outputs = List.of();

    private boolean isComplete;

    private static final Logger LOGGER = LogUtils.getLogger();

    public CraftingMachineTask(@NotNull Level level, MachineInventory inventory, T recipeInput,
            @Nullable MultiSlotAccess outputSlots, @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, null, recipeInput, outputSlots, recipe);
    }

    public CraftingMachineTask(@NotNull Level level, MachineInventory inventory,
            @Nullable MachineFluidHandler fluidHandler, T recipeInput, @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, fluidHandler, recipeInput, null, recipe);
    }

    public CraftingMachineTask(@NotNull Level level, MachineInventory inventory,
            @Nullable MachineFluidHandler fluidHandler, T recipeInput, @Nullable MultiSlotAccess outputSlots,
            @Nullable RecipeHolder<R> recipe) {
        this.level = level;
        this.inventory = inventory;
        this.fluidHandler = fluidHandler;
        this.recipeInput = recipeInput;
        this.outputSlots = outputSlots;
        this.recipe = recipe;
        inventory.updateMachineState(MachineState.FULL_OUTPUT, false);
        inventory.updateMachineState(MachineState.EMPTY_INPUT, true);
    }

    public MachineInventory getInventory() {
        return inventory;
    }

    @Nullable
    public RecipeHolder<R> getRecipeHolder() {
        return recipe;
    }

    // TODO: NEO-PORT: Should this return the holder?
    @Nullable
    public R getRecipe() {
        return recipe.value();
    }

    @Nullable
    public ResourceLocation getRecipeId() {
        return recipe.id();
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

        // If the recipe failed to load somehow, cancel
        if (recipe == null) {
            isComplete = true;
            return;
        }

        // Get the outputs list.
        if (!hasDeterminedOutputs) {
            hasDeterminedOutputs = true;
            T processedRecipeInput = prepareToDetermineOutputs(recipe.value(), recipeInput);
            outputs = recipe.value().craft(processedRecipeInput, level.registryAccess());

            // TODO: Compact any items that are the same into singular stacks?

            // Store the recipe energy cost.
            progressRequired = getProgressRequired(recipe.value());
        }

        // If we don't have a recipe match, complete the task and wait for a new one.
        if (!recipe.value().matches(recipeInput, level)) {
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
                consumeInputs(recipe.value());

                // The receiver was able to take the outputs, task complete.
                isComplete = true;
            }
        }
    }

    @Override
    public float getProgress() {
        if (recipe == null) {
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
        MachineInventory snapshot = getInventory().snapshot(outputSlots); // Snapshot for chained simulations.
        for (OutputStack output : outputs) {
            ItemStack item = output.getItem();

            for (SingleSlotAccess outputAccess : outputSlots.getAccesses()) {
                item = outputAccess.insertItem(snapshot, item, false); // Don't simulate, we are using a snapshot copy
            }

            // If we fail, say we can't accept these outputs
            if (!item.isEmpty()) {
                return false;
            }
        }

        // If we're not simulating, go for it
        if (!simulate) {
            for (OutputStack output : outputs) {
                ItemStack item = output.getItem();

                for (SingleSlotAccess outputAccess : outputSlots.getAccesses()) {
                    item = outputAccess.insertItem(inventory, item, false);
                }
            }
        }

        return true;
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
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();

        // If the recipe is null, we aren't going to keep the task
        if (recipe == null) {
            return tag;
        }

        tag.putString(KEY_RECIPE_ID, recipe.id().toString());
        tag.putInt(KEY_PROGRESS_MADE, progressMade);
        tag.putInt(KEY_PROGRESS_REQUIRED, progressRequired);
        tag.putBoolean(KEY_HAS_COLLECTED_INPUTS, hasConsumedInputs);
        tag.putBoolean(KEY_IS_COMPLETE, isComplete);

        tag.putBoolean(KEY_HAS_DETERMINED_OUTPUTS, hasDeterminedOutputs);
        if (hasDeterminedOutputs) {
            ListTag outputsNbt = new ListTag();
            for (OutputStack stack : outputs) {
                outputsNbt.add(stack.serializeNBT(lookupProvider));
            }
            tag.put(KEY_OUTPUTS, outputsNbt);
        }

        return tag;
    }

    // TODO: 20.6: Swap tasks to use Codecs.
    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        // TODO: Exception handling
        recipe = loadRecipe(ResourceLocation.parse(nbt.getString(KEY_RECIPE_ID)));
        progressMade = nbt.getInt(KEY_PROGRESS_MADE);
        progressRequired = nbt.getInt(KEY_PROGRESS_REQUIRED);
        hasConsumedInputs = nbt.getBoolean(KEY_HAS_COLLECTED_INPUTS);
        isComplete = nbt.getBoolean(KEY_IS_COMPLETE);

        hasDeterminedOutputs = nbt.getBoolean(KEY_HAS_DETERMINED_OUTPUTS);
        if (hasDeterminedOutputs) {
            ListTag outputsNbt = nbt.getList(KEY_OUTPUTS, Tag.TAG_COMPOUND);
            outputs = new ArrayList<>();
            for (Tag tag : outputsNbt) {
                outputs.add(OutputStack.fromNBT(lookupProvider, (CompoundTag) tag));
            }
        }
    }

    @Nullable
    protected RecipeHolder<R> loadRecipe(ResourceLocation id) {
        try {
            // noinspection unchecked
            return (RecipeHolder<R>) level.getRecipeManager().byKey(id).orElse(null);
        } catch (ClassCastException ex) {
            // Can occur when loading a world with the old smelting recipe system.
            LOGGER.warn("Failed to cast recipe '{}' to the correct type, not loading in-progress recipe.", id);
            return null;
        }
    }

    // endregion
}
