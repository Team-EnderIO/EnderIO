package com.enderio.enderio.content.misc_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class IndustrialInsulationBlock extends SpongeBlock {
    private static final int MAX_REPLACES = 64;
    private static final int MAX_RANGE = 6;

    public IndustrialInsulationBlock(Properties props) {
        super(props);
    }

    private void removeBlock(BlockPos block, Level level) {
        level.setBlock(block, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void tryAbsorbWater(Level level, BlockPos pos) {
        // 26.2-port: net.minecraft.util.Tuple was removed — using a local record instead.
        record PosRange(BlockPos pos, int range) {}
        Deque<PosRange> queue = new ArrayDeque<>();
        queue.add(new PosRange(pos, 0));
        int checkedBlocksCount = 0;

        while (!queue.isEmpty()) {
            PosRange entry = queue.poll();
            BlockPos blockpos = entry.pos();
            int currentRange = entry.range();

            for (Direction direction : Direction.values()) {
                BlockPos blockToCheckPos = blockpos.relative(direction);
                BlockState blockToCheckState = level.getBlockState(blockToCheckPos);
                Block blockToCheck = blockToCheckState.getBlock();

                if (blockToCheck instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, blockToCheckPos, blockToCheckState).isEmpty()) {
                    ++checkedBlocksCount;

                    if (currentRange < MAX_RANGE) {
                        queue.add(new PosRange(blockToCheckPos, currentRange + 1));
                    }

                } else if (blockToCheckState.getBlock() instanceof LiquidBlock) {
                    if (blockToCheckState.is(Blocks.KELP) || blockToCheckState.is(Blocks.KELP_PLANT) || blockToCheckState.is(Blocks.SEAGRASS) || blockToCheckState.is(Blocks.TALL_SEAGRASS)) {
                        BlockEntity blockEntity = blockToCheckState.hasBlockEntity() ? level.getBlockEntity(blockToCheckPos) : null;
                        dropResources(blockToCheckState, level, blockToCheckPos, blockEntity);
                    }
                    this.removeBlock(blockToCheckPos, level);
                    ++checkedBlocksCount;

                    if (currentRange < MAX_RANGE) {
                        queue.add(new PosRange(blockToCheckPos, currentRange + 1));
                    }
                }
            }

            if (checkedBlocksCount > MAX_REPLACES) {
                break;
            }
        }
    }
}
