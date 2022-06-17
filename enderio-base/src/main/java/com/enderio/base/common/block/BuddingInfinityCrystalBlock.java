package com.enderio.base.common.block;

import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.Random;

public class BuddingInfinityCrystalBlock extends BuddingAmethystBlock {
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingInfinityCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[randomSource.nextInt(DIRECTIONS.length)];
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = null;
            if (canClusterGrowAtState(blockstate)) {
                block = EIOBlocks.SMALL_INFINITY_BUD.get();
            } else if (blockstate.is(EIOBlocks.SMALL_INFINITY_BUD.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = EIOBlocks.MEDIUM_INFINITY_BUD.get();
            } else if (blockstate.is(EIOBlocks.MEDIUM_INFINITY_BUD.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = EIOBlocks.LARGE_INFINITY_BUD.get();
            } else if (blockstate.is(EIOBlocks.LARGE_INFINITY_BUD.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = EIOBlocks.INFINITY_CRYSTAL_CLUSTER.get();
            }

            if (block != null) {
                BlockState blockstate1 = block
                    .defaultBlockState()
                    .setValue(AmethystClusterBlock.FACING, direction)
                    .setValue(AmethystClusterBlock.WATERLOGGED, blockstate.getFluidState().getType() == Fluids.WATER);
                level.setBlockAndUpdate(blockpos, blockstate1);
            }
        }
    }
}
