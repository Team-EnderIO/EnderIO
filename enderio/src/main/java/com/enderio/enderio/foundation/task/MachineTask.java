package com.enderio.enderio.foundation.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface MachineTask extends INBTSerializable<CompoundTag> {
    void tick();

    float getProgress();

    boolean isCompleted();
}
