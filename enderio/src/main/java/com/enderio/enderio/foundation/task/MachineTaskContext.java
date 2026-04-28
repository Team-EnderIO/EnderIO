package com.enderio.enderio.foundation.task;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface MachineTaskContext {
    BlockPos getBlockPos();
    BlockEntity getBlockEntity();
    @Nullable ServerLevel getLevel();
}
