package com.enderio.enderio.foundation.task;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface MachineTask extends ValueIOSerializable {
    void tick();

    float getProgress();

    boolean isCompleted();
}
