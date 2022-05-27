package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class PoweredTask implements INBTSerializable<CompoundTag> {
    protected final IMachineEnergyStorage energyStorage;

    /**
     * Create a new powered task.
     * @param energyStorage The energy storage used to power the task.
     */
    public PoweredTask(IMachineEnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    public abstract void tick();

    public abstract float getProgress();

    public abstract boolean isComplete();
}
