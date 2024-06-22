package com.enderio.machines.common.blockentity.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface MachineTask {
    void tick();

    float getProgress();

    boolean isCompleted();

    Tag save();

    void load(CompoundTag tag);
}
