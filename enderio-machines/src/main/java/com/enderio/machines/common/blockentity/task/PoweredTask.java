package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A task that performs an action by consuming energy.
 */
public abstract class PoweredTask implements INBTSerializable<CompoundTag> {
    /**
     * The energy storage the task consumes from.
     */
    protected final IMachineEnergyStorage energyStorage;

    /**
     * Create a new powered task.
     * @param energyStorage The energy storage used to power the task.
     */
    public PoweredTask(IMachineEnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    /**
     * Tick the task. Will consume energy to continue.
     */
    public abstract void tick();

    /**
     * Get the progress of the task.
     * 0 = not begun
     * 1 = finished.
     * This is for GUI mostly.
     */
    public abstract float getProgress();

    /**
     * @return Whether the task is complete and can be replaced.
     */
    public abstract boolean isComplete();
}
