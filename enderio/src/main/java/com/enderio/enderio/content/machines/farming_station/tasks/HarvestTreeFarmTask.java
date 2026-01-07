package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import com.enderio.enderio.foundation.util.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public class HarvestTreeFarmTask implements FarmTask {
    public static final HarvestTreeFarmTask INSTANCE = new HarvestTreeFarmTask();

    private HarvestTreeFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        BlockPos bottom = targetBlock.above();
        BlockState bottomState = blockEntity.getLevel().getBlockState(bottom);
        AABB range = new AABB(blockEntity.getPosition())
            .inflate(blockEntity.getFarmingRange())
            .inflate(4, 8, 4) // increase range to accommodate for extra leaves
            .move(0, 8, 0);
        if (bottomState.is(BlockTags.LOGS)) {
            Set<BlockPos> tree = TreeHelper.getTree(blockEntity.getLevel(), bottom, pos -> range.contains(pos.getX(), pos.getY(), pos.getZ()));
            for (BlockPos pos : tree) {
                BlockState state = blockEntity.getLevel().getBlockState(pos);
                if (state.is(BlockTags.LOGS)) {
                    if (blockEntity.getAxe().isEmpty()) {
                        return FarmInteraction.BLOCKED;
                    }

                    if (!blockEntity.handleDrops(state, pos, targetBlock, blockEntity, blockEntity.getAxe())) {
                        return FarmInteraction.BLOCKED;
                    }
                    blockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    blockEntity.getAxe().mineBlock(blockEntity.getLevel(), state, pos, blockEntity.getPlayer());
                } else if (state.is(BlockTags.LEAVES)) {
                    if (!blockEntity.handleDrops(state, pos, targetBlock, blockEntity, blockEntity.getShears())) {
                        return FarmInteraction.BLOCKED;
                    }
                    blockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    blockEntity.getShears().mineBlock(blockEntity.getLevel(), state, pos, blockEntity.getPlayer());
                }
            }
            return FarmInteraction.FINISHED;

        }
        return FarmInteraction.IGNORED;
    }
}
