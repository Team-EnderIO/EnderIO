package com.enderio.machines.common.blockentity.farming.farmers;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MelonBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PumpkinFarmer implements IFarmer {

    @Override
    public boolean canHarvest(Level level, BlockPos pos, BlockState blockState) {
        return level != null && blockState.getBlock() instanceof PumpkinBlock || blockState.getBlock() instanceof MelonBlock;
    }

    @Override
    public SoundEvent getHarvestSound() {
        return SoundEvents.WOOD_BREAK;
    }
}
