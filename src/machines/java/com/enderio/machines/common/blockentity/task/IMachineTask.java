package com.enderio.machines.common.blockentity.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMachineTask extends INBTSerializable<CompoundTag> {
    void tick();

    float getProgress();

    // TODO: I want to rename it to isCompleted.
    boolean isComplete();
}
