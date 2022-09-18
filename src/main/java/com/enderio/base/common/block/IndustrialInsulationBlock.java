package com.enderio.base.common.block;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

import java.util.List;
import java.util.Queue;

public class IndustrialInsulationBlock extends SpongeBlock {
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
    protected boolean absorb(Level level, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        List<BlockPos> list = Lists.newArrayList();
        queue.add(new Tuple(pos, Integer.valueOf(0)));
        int i = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB().intValue();

            for (Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);

                BlockState blockToCheck = level.getBlockState(blockpos1);
                if ( blockToCheck.getBlock() instanceof LiquidBlock || blockToCheck.getMaterial() == Material.WATER || blockToCheck.getMaterial() == Material.LAVA) {
                    level.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 2);
                    list.add(blockpos1);
                    ++i;

                    if (j < 6) {
                        queue.add(new Tuple(blockpos1, j + 1));
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }
}
