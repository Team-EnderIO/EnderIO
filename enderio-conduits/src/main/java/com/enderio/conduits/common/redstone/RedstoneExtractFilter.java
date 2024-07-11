package com.enderio.conduits.common.redstone;

import com.enderio.base.api.filter.ResourceFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface RedstoneExtractFilter extends ResourceFilter {

    int getInputSignal(Level level, BlockPos pos, Direction direction);

}
