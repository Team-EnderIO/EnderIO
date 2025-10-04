package com.enderio.enderio.common.filters.redstone;

import com.enderio.enderio.api.filter.RedstoneInputFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class RedstoneSensorFilter implements RedstoneInputFilter {

    public static final RedstoneSensorFilter INSTANCE = new RedstoneSensorFilter();

    private RedstoneSensorFilter() {
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos).getAnalogOutputSignal(level, pos);
    }
}
