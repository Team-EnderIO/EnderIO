package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredCraftingMachineTask<R extends MachineRecipe<C>, C extends Container>
    extends CraftingMachineTask<R, C> implements IPoweredMachineTask {

    private final IMachineEnergyStorage energyStorage;

    public PoweredCraftingMachineTask(@NotNull Level level, MachineInventory inventory, IMachineEnergyStorage energyStorage, C container, MultiSlotAccess outputSlots, @Nullable R recipe) {
        super(level, inventory, container, outputSlots, recipe);
        this.energyStorage = energyStorage;
    }

    public PoweredCraftingMachineTask(@NotNull Level level, MachineInventory inventory, IMachineEnergyStorage energyStorage, C container, SingleSlotAccess outputSlot, @Nullable R recipe) {
        this(level, inventory, energyStorage, container, outputSlot.wrapToMulti(), recipe);
    }

    @Override
    public IMachineEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected int makeProgress(int remainingProgress) {
        return energyStorage.consumeEnergy(remainingProgress, false);
    }

    @Override
    protected int getProgressRequired(R recipe) {
        return recipe.getEnergyCost(container);
    }
}
