package com.enderio.machines.common.blockentity.task;

import com.enderio.api.recipe.IMachineRecipe;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A crafting task that consumes energy.
 */
public abstract class PoweredCraftingTask<T extends IMachineRecipe<T, Container>> extends PoweredTask {

    // TODO: Might have to deal with loading before level is done here. Maybe save ResourceLocation of recipe and query it on first tick?

    /**
     * The recipe being crafted.
     */
    private final T recipe;

    private final Container container;

    /**
     * Amount of energy consumed to craft so far.
     */
    private int energyConsumed;

    /**
     * Whether or not inputs have been collected.
     */
    private boolean collectedInputs;

    // TODO: Do we store the claimed ingredients here in a kind of pseudo inventory? Would allow for some small benefits.

    /**
     * Whether the recipe craft is complete.
     * Will not be true until the inventory has the result item.
     */
    private boolean complete;

    /**
     * Create a new powered crafting task.
     *
     * @param energyStorage The energy storage used to power the task.
     */
    public PoweredCraftingTask(IMachineEnergyStorage energyStorage, T recipe, Container container) {
        super(energyStorage);
        this.recipe = recipe;
        this.container = container;
    }

    public static <T extends IMachineRecipe<T, Container>> PoweredCraftingTask<T> create() {
        return null;
    }

    protected abstract boolean takeOutputs(List<ItemStack> outputs);

    @Override
    public void tick() {
        // Try to consume as much energy as possible to finish the craft.
        if (energyConsumed <= recipe.getEnergyCost()) {
            energyConsumed += energyStorage.consumeEnergy(recipe.getEnergyCost() - energyConsumed);
        }

        // If the recipe has been crafted, attempt to put it into storage
        if (energyConsumed >= recipe.getEnergyCost()) {
            // Perform the craft
            List<ItemStack> outputs = recipe.craft(container);

            // Attempt to add these to the inventory
            if (takeOutputs(outputs)) {
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

    // TODO

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    // endregion

    private class TaskContainer implements Container {

        private List<ItemStack> inputs;

        @Override
        public int getContainerSize() {
            return inputs.size() + recipe.getOutputCount(this);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getItem(int index) {
            return null;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            return null;
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            return null;
        }

        @Override
        public void setItem(int index, ItemStack stack) {

        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void clearContent() {

        }
    }
}
