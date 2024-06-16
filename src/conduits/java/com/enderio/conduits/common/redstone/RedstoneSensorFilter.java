package com.enderio.conduits.common.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;

public class RedstoneSensorFilter implements RedstoneExtractFilter{

    public static final RedstoneSensorFilter INSTANCE = new RedstoneSensorFilter();

    private RedstoneSensorFilter() {
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ComparatorBlockEntity comp ? comp.getOutputSignal() : 0;
    }
}
