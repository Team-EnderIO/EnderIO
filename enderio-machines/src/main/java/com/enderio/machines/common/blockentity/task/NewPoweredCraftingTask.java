package com.enderio.machines.common.blockentity.task;

import com.enderio.api.recipe.IMachineRecipe;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NewPoweredCraftingTask extends PoweredTask {
    private Level level;
    private Container container;

    private IMachineRecipe<?, Container> recipe;

    /**
     * Amount of energy consumed to craft so far.
     */
    private int energyConsumed;

    /**
     * Whether or not inputs have been collected.
     */
    private boolean collectedInputs;

    /**
     * Whether the recipe craft is complete.
     * Will not be true until the inventory has the result item.
     */
    private boolean complete;

    private @Nullable CompoundTag recipeToLoad;

    public NewPoweredCraftingTask(IMachineEnergyStorage energyStorage, @Nullable IMachineRecipe<?, Container> recipe, Level level, Container container) {
        super(energyStorage);
        this.recipe = recipe;
        this.level = level;
        this.container = container;
    }

    public final IMachineRecipe<?, Container> getRecipe() {
        return recipe;
    }

    protected abstract boolean takeOutputs(List<ItemStack> outputs, boolean simulate);

    @Override
    public void tick() {
        // TODO: 28/05/2022 Don't start if the output cannot be taken

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

        // If we can't output, cancel the task. However if for some reason we can't output after the inputs are collected, don't.
        if (!collectedInputs && !takeOutputs(recipe.craft(container), true)) {
            complete = true;
            return;
        }

        // If we haven't done so already, consume inputs for the recipe.
        if (!collectedInputs) {
            recipe.consumeInputs(container);
            collectedInputs = true;
        }

        // Try to consume as much energy as possible to finish the craft.
        if (energyConsumed <= recipe.getEnergyCost()) {
            energyConsumed += energyStorage.consumeEnergy(recipe.getEnergyCost() - energyConsumed);
        }

        // If the recipe has been crafted, attempt to put it into storage
        if (energyConsumed >= recipe.getEnergyCost()) {
            // Perform the craft
            List<ItemStack> outputs = recipe.craft(container);

            // Attempt to add these to the inventory
            if (takeOutputs(outputs, false)) {
                // The receiver was able to take the outputs, task complete.
                complete = true;
            }
        }
    }

    @Override
    public float getProgress() {
        return energyConsumed / (float) recipe.getEnergyCost();
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
        tag.putBoolean("collected_inputs", collectedInputs);
        tag.putBoolean("complete", complete);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        recipeToLoad = nbt.getCompound("recipe").copy();
        energyConsumed = nbt.getInt("energy_consumed");
        collectedInputs = nbt.getBoolean("collected_inputs");
        complete = nbt.getBoolean("complete");
    }

    protected CompoundTag serializeRecipe(CompoundTag tag, IMachineRecipe<?, Container> recipe) {
        tag.putString("id", recipe.getId().toString());
        return tag;
    }

    protected @Nullable IMachineRecipe<?, Container> loadRecipe(CompoundTag nbt) {
        ResourceLocation id = new ResourceLocation(nbt.getString("id"));
        return (IMachineRecipe<?, Container>) level.getRecipeManager().byKey(id).orElse(null);
    }

    // endregion

    // TODO: For the builder pattern, we'll use this once we need getInputs too (when we phase out direct use of Container).
    public interface OutputAcceptor {
        boolean accept(List<ItemStack> outputs, boolean simulate);
    }
}
