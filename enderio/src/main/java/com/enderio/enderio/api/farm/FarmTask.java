package com.enderio.enderio.api.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface FarmTask {

    <T extends BlockEntity & FarmingMachine>
    FarmInteraction process(BlockPos targetBlock, T blockEntity);
}
