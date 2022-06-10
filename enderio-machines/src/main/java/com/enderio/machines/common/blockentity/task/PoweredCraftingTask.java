package com.enderio.machines.common.blockentity.task;

import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.api.machines.recipes.MachineRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <C> The container type used by the recipes. Mostly useful for {@link net.minecraft.world.item.crafting.CraftingRecipe}'s.
 */
public abstract class PoweredCraftingTask<R extends MachineRecipe<C>, C extends Container> extends PoweredTask {
    private final Level level;
    private final C container;

    private R recipe;

    /**
     * Amount of energy consumed to craft so far.
     */
    private int energyConsumed;

    /**
     * Amount of energy needed to be consumed.
     * Stored because the recipe needs access to the container to determine energy cost. Once inputs are consumed, we can't query again.
     */
    private int energyCost;

    /**
     * Whether or not inputs have been collected.
     */
    private boolean collectedInputs;

    /**
     * Whether the outputs have been determined yet.
     */
    private boolean determinedOutputs;

    /**
     * The outputs of the recipe.
     * These are determined after we take ingredients, as we don't want the outputs to change later.
     */
    private List<OutputStack> outputs;

    /**
     * Whether the recipe craft is complete.
     * Will not be true until the inventory has the result item.
     */
    private boolean complete;

    private @Nullable CompoundTag recipeToLoad;

    public PoweredCraftingTask(IMachineEnergyStorage energyStorage, @Nullable R recipe, Level level, C container) {
        super(energyStorage);
        this.recipe = recipe;
        this.level = level;
        this.container = container;
    }

    public final R getRecipe() {
        return recipe;
    }

    protected abstract void takeInputs(R recipe);

    protected abstract boolean takeOutputs(List<OutputStack> outputs, boolean simulate);

    @Override
    public void tick() {
        // If the recipe is done, don't let it tick.
        if (complete)
            return;

        // If we have a recipe ready to load up, load it.
        if (recipeToLoad != null) {
            // Attempt to load the saved recipe.
            recipe = loadRecipe(recipeToLoad);
            recipeToLoad = null;
        }

        // If the recipe load fails, ignore it and consider the task complete.
        if (recipe == null) {
            complete = true;
            return;
        }

        // Get the outputs list.
        if (!determinedOutputs) {
            outputs = recipe.craft(container);

            // TODO: Compact any items that are the same into singular stacks.

            determinedOutputs = true;
        }

        // If we can't inputs or outputs, cancel the task. However if for some reason we can't output after the inputs are collected, don't.
        if (!collectedInputs && (!takeOutputs(outputs, true) || !recipe.matches(container, level))) {
            complete = true;
            return;
        }

        // If we haven't done so already, consume inputs for the recipe.
        if (!collectedInputs) {
            // Consume inputs for the recipe.
            takeInputs(recipe);
            collectedInputs = true;

            // Store the recipe energy cost.
            // This is run afterwards as it allows container context changes after takeInputs()
            energyCost = recipe.getEnergyCost(container);
        }

        // Try to consume as much energy as possible to finish the craft.
        if (energyConsumed <= energyCost) {
            energyConsumed += energyStorage.consumeEnergy(energyCost - energyConsumed);
        }

        // If the recipe has been crafted, attempt to put it into storage
        if (energyConsumed >= energyCost) {
            // Attempt to complete the craft
            if (takeOutputs(outputs, false)) {
                // The receiver was able to take the outputs, task complete.
                complete = true;
            }
        }
    }

    @Override
    public float getProgress() {
        return energyConsumed / (float) recipe.getEnergyCost(container);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    // region Serialization

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("recipe", serializeRecipe(new CompoundTag(), recipe));
        tag.putInt("energy_consumed", energyConsumed);
        tag.putInt("energy_cost", energyConsumed);
        tag.putBoolean("collected_inputs", collectedInputs);
        tag.putBoolean("complete", complete);

        tag.putBoolean("determined_outputs", determinedOutputs);
        if (determinedOutputs) {
            ListTag outputsNbt = new ListTag();
            for (OutputStack stack : outputs) {
                outputsNbt.add(stack.serializeNBT());
            }
            tag.put("outputs", outputsNbt);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        recipeToLoad = nbt.getCompound("recipe").copy();
        energyConsumed = nbt.getInt("energy_consumed");
        energyCost = nbt.getInt("energy_cost");
        collectedInputs = nbt.getBoolean("collected_inputs");
        complete = nbt.getBoolean("complete");

        determinedOutputs = nbt.getBoolean("determined_outputs");
        if (determinedOutputs) {
            ListTag outputsNbt = nbt.getList("outputs", Tag.TAG_COMPOUND);
            outputs = new ArrayList<>();
            for (Tag tag : outputsNbt) {
                outputs.add(OutputStack.fromNBT((CompoundTag) tag));
            }
        }
    }

    protected CompoundTag serializeRecipe(CompoundTag tag, R recipe) {
        if (recipe != null) {
            tag.putString("id", recipe.getId().toString());
        }
        return tag;
    }

    protected @Nullable R loadRecipe(CompoundTag nbt) {
        if (nbt.contains("id")) {
            ResourceLocation id = new ResourceLocation(nbt.getString("id"));
            return (R) level.getRecipeManager().byKey(id).orElse(null);
        }
        return null;
    }

    // endregion

    // TODO: For the builder pattern, we'll use this once we need getInputs too (when we phase out direct use of Container).
    public interface OutputAcceptor {
        boolean accept(List<ItemStack> outputs, boolean simulate);
    }
}
