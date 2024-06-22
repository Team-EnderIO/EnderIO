package com.enderio.machines.common.blockentity.task;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.recipe.MachineRecipe;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO: A recipe interface that doesn't require power :)
public abstract class CraftingMachineTask<R extends MachineRecipe<T>, T extends RecipeInput>
    implements MachineTask {

    protected final Level level;
    protected final MachineInventory inventory;
    protected final MultiSlotAccess outputSlots;
    protected final T recipeInput;

    @Nullable
    private RecipeHolder<R> recipe;

    private int progressMade;
    private int progressRequired;

    private boolean hasConsumedInputs;

    private List<OutputStack> outputs = List.of();

    private boolean isComplete;

    public CraftingMachineTask(@NotNull Level level, MachineInventory inventory, T recipeInput, MultiSlotAccess outputSlots, @Nullable RecipeHolder<R> recipe) {
        this.level = level;
        this.inventory = inventory;
        this.recipeInput = recipeInput;
        this.outputSlots = outputSlots;
        this.recipe = recipe;
        inventory.updateMachineState(MachineState.FULL_OUTPUT, false);
        inventory.updateMachineState(MachineState.EMPTY_INPUT, true);
    }

    public MachineInventory getInventory() {
        return inventory;
    }

    // TODO: NEO-PORT: Should this return the holder?
    @Nullable
    public R getRecipe() {
        return recipe.value();
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
        if (outputs.isEmpty()) {
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

        // See that we can add all the outputs
        for (OutputStack output : outputs) {
            ItemStack item = output.getItem();

            for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
                item = outputAccess.insertItem(inventory, item, true);
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

                for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
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
    public CompoundTag save() {
        CraftingTaskData data = new CraftingTaskData(this.recipe.id().toString(), this.progressMade, this.progressRequired, this.hasConsumedInputs, this.isComplete, this.outputs);
        DataResult<Tag> encode = CraftingTaskData.CODEC.encode(data, NbtOps.INSTANCE, new CompoundTag());
        return (CompoundTag) encode.getOrThrow();
    }

    @Override
    public void load(CompoundTag tag) {
        DataResult<Pair<CraftingTaskData, Tag>> decode = CraftingTaskData.CODEC.decode(NbtOps.INSTANCE, tag);
        CraftingTaskData craftingTaskData = decode.getOrThrow().getFirst();
        recipe = loadRecipe(ResourceLocation.parse(craftingTaskData.recipeId));
        progressMade = craftingTaskData.progressMade;
        progressRequired = craftingTaskData.progressRequired;
        hasConsumedInputs = craftingTaskData.hasConsumedInputs;
        isComplete = craftingTaskData.isComplete;
        outputs = craftingTaskData.outputs;
    }

//    @Override
//    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
//        CompoundTag tag = new CompoundTag();
//
//        // If the recipe is null, we aren't going to keep the task
//        if (recipe == null) {
//            return tag;
//        }
//
//        tag.putString(KEY_RECIPE_ID, recipe.id().toString());
//        tag.putInt(KEY_PROGRESS_MADE, progressMade);
//        tag.putInt(KEY_PROGRESS_REQUIRED, progressRequired);
//        tag.putBoolean(KEY_HAS_COLLECTED_INPUTS, hasConsumedInputs);
//        tag.putBoolean(KEY_IS_COMPLETE, isComplete);
//
//        tag.putBoolean(KEY_HAS_DETERMINED_OUTPUTS, hasDeterminedOutputs);
//        if (hasDeterminedOutputs) {
//            ListTag outputsNbt = new ListTag();
//            for (OutputStack stack : outputs) {
//                outputsNbt.add(stack.serializeNBT(lookupProvider));
//            }
//            tag.put(KEY_OUTPUTS, outputsNbt);
//        }
//
//        return tag;
//    }
//
//    // TODO: 20.6: Swap tasks to use Codecs.
//    @Override
//    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
//        // TODO: Exception handling
//        recipe = loadRecipe(ResourceLocation.parse(nbt.getString(KEY_RECIPE_ID)));
//        progressMade = nbt.getInt(KEY_PROGRESS_MADE);
//        progressRequired = nbt.getInt(KEY_PROGRESS_REQUIRED);
//        hasConsumedInputs = nbt.getBoolean(KEY_HAS_COLLECTED_INPUTS);
//        isComplete = nbt.getBoolean(KEY_IS_COMPLETE);
//
//        hasDeterminedOutputs = nbt.getBoolean(KEY_HAS_DETERMINED_OUTPUTS);
//        if (hasDeterminedOutputs) {
//            ListTag outputsNbt = nbt.getList(KEY_OUTPUTS, Tag.TAG_COMPOUND);
//            outputs = new ArrayList<>();
//            for (Tag tag : outputsNbt) {
//                outputs.add(OutputStack.fromNBT(lookupProvider, (CompoundTag) tag));
//            }
//        }
//    }

    @Nullable
    protected RecipeHolder<R> loadRecipe(ResourceLocation id) {
        //noinspection unchecked
        return (RecipeHolder<R>) level.getRecipeManager().byKey(id).orElse(null);
    }

    public record CraftingTaskData(String recipeId, int progressMade, int progressRequired, boolean hasConsumedInputs, boolean isComplete, List<OutputStack> outputs) {

        public static Codec<CraftingTaskData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("recipeid").forGetter(CraftingTaskData::recipeId),
            Codec.INT.fieldOf("progressmade").forGetter(CraftingTaskData::progressMade),
            Codec.INT.fieldOf("progressRequired").forGetter(CraftingTaskData::progressRequired),
            Codec.BOOL.fieldOf("hasconsumedinputs").forGetter(CraftingTaskData::hasConsumedInputs),
            Codec.BOOL.fieldOf("iscomplete").forGetter(CraftingTaskData::isComplete),
            OutputStack.CODEC.listOf().optionalFieldOf("outputs", List.of()).forGetter(CraftingTaskData::outputs)
        ).apply(instance, CraftingTaskData::new));
    }
    // endregion
}
