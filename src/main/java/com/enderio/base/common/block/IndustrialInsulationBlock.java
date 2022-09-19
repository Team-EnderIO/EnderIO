package com.enderio.base.common.block;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Arrays;
import java.util.Queue;

public class IndustrialInsulationBlock extends SpongeBlock {
    private final int DIRECTIONS_COUNT = Direction.values().length;

    public IndustrialInsulationBlock(Properties props) {
        super(props);
    }

    @Override
    protected void tryAbsorbWater(Level level, BlockPos pos) {
        absorb(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        absorb(level, pos);
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    // Overriding sponge default behavior, easier than overriding the whole BlockSponge and removing all the BlockState code
    protected void absorb(Level level, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();

        // Inserts this block on the queue
        queue.add(new Tuple<>(pos, 0));
        int i = 0;


        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for (Direction direction : Direction.values()) {
                // Get the nearest block in the current direction
                BlockPos blockToCheck = blockpos.relative(direction);
                BlockState blockToCheckState = level.getBlockState(blockToCheck);

                if (blockToCheckState.getBlock() instanceof LiquidBlock || blockToCheckState.getMaterial() == Material.WATER || blockToCheckState.getMaterial() == Material.LAVA) {
                    level.setBlock(blockToCheck, Blocks.AIR.defaultBlockState(), 2);
                    ++i;

                    if (j < this.DIRECTIONS_COUNT) {
                        queue.add(new Tuple<>(blockToCheck, j + 1));
                    }
                }
            }

            int MAX_RANGE = 64;
            if (i > MAX_RANGE) {
                break;
            }
        }
    }
}
