package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;

public class SilentPressurePlateBlock extends PressurePlateBlock {

    public SilentPressurePlateBlock(PressurePlateBlock wrapped) {
        super(wrapped.sensitivity, Properties.copy(wrapped));
    }

    @Override
    protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {}

    @Override
    protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {}

}
