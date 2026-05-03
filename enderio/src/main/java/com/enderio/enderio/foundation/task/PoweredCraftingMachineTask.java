package com.enderio.enderio.foundation.task;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotKey;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.energy.MachineEnergyHandler;
import com.enderio.enderio.foundation.state.MachineStateUpdater;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public abstract class PoweredCraftingMachineTask<R extends MachineRecipe<C>, C extends RecipeInput>
        extends CraftingMachineTask<R, C>
        implements PoweredMachineTask {

    private final MachineEnergyHandler energyStorage;

    public PoweredCraftingMachineTask(
        Level level,
        MachineStateUpdater machineStateUpdater,
        ItemStorage inventory,
        @Nullable FluidStorage fluidStorage,
        MachineEnergyHandler energyStorage,
        C container,
        ResourceSlotKey<ItemResource> outputSlots,
        @Nullable RecipeHolder<R> recipe) {
        super(level, machineStateUpdater, inventory, fluidStorage, container, outputSlots, recipe);
        this.energyStorage = energyStorage;
    }

    public PoweredCraftingMachineTask(
        Level level,
        MachineStateUpdater machineStateUpdater,
        ItemStorage inventory,
        MachineEnergyHandler energyStorage,
        C container,
        ResourceSlotKey<ItemResource> outputSlots,
        @Nullable RecipeHolder<R> recipe) {
        this(level, machineStateUpdater, inventory, null, energyStorage, container, outputSlots, recipe);
    }

    @Override
    public MachineEnergyHandler getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected int makeProgress(int remainingProgress) {
        return energyStorage.consume(remainingProgress, null);
    }

    @Override
    protected int getProgressRequired(R recipe) {
        return recipe.getEnergyCost(recipeInput);
    }
}
