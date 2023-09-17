package com.enderio.machines.common.blockentity.farming.farmers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface IFarmer {

    boolean canHarvest(Level level, BlockPos pos, BlockState blockState);

    default List<ItemStack> doOperation(Level level, BlockPos pos, BlockState blockState, boolean simulate) {
        NonNullList<ItemStack> items = NonNullList.create();

        if (canHarvest(level, pos, blockState)) {
            items.addAll(Block.getDrops(blockState, (ServerLevel) level, pos, null));
            if (!simulate) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, getHarvestSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
        return items;
    }

    default int getCostPerOperation() {
        return 120;
    }
    SoundEvent getHarvestSound();

}
