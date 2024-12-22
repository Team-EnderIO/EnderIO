package com.enderio.machines.common.blocks.base.task;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface MachineTask extends INBTSerializable<CompoundTag> {
    void tick();

    float getProgress();

    boolean isCompleted();
}
