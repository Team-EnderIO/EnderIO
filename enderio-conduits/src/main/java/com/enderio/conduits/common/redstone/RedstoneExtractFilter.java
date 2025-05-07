package com.enderio.conduits.common.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface RedstoneExtractFilter {

    int getInputSignal(Level level, BlockPos pos, Direction direction);

}
