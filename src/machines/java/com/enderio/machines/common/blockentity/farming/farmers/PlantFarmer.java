package com.enderio.machines.common.blockentity.farming.farmers;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantFarmer implements IFarmer{

    @Override
    public boolean canHarvest(Level level, BlockPos pos, BlockState blockState) {
        return level != null && blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMaxAge(blockState);
    }

    @Override
    public SoundEvent getHarvestSound() {
        return SoundEvents.CROP_BREAK;
    }
}
