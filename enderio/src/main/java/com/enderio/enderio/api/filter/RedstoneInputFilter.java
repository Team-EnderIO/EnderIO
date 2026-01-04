package com.enderio.enderio.api.filter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneInputFilter extends RedstoneFilter {

    // TODO: Should we wrap level, pos and direction with a RedstoneInputContext?
    int getInputSignal(Level level, BlockPos pos, Direction direction);

}
