package com.enderio.enderio.conduits.common.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import com.enderio.enderio.api.filter.RedstoneInputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

public class RedstoneNOTFilter implements RedstoneOutputFilter, RedstoneInputFilter {

    public static final RedstoneNOTFilter INSTANCE = new RedstoneNOTFilter();

    private RedstoneNOTFilter() {
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        return context.isActive(control) ? 0 : 15;
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        return level.getSignal(pos, direction) == 0 ? 15 : 0;
    }
}
