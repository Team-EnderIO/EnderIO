package com.enderio.enderio.machines.common.blocks.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
import com.enderio.enderio.machines.common.utility.TreeHelper;
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
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        BlockPos bottom = soil.above();
        BlockState bottomState = farmBlockEntity.getLevel().getBlockState(bottom);
        AABB range = new AABB(farmBlockEntity.getPosition()).inflate(farmBlockEntity.getFarmingRange());
        if (bottomState.is(BlockTags.LOGS)) {
            Set<BlockPos> tree = TreeHelper.getTree(farmBlockEntity.getLevel(), bottom,
                pos -> range.contains(pos.getX(), pos.getY(), pos.getZ()));
            if (farmBlockEntity.getConsumedPower() >= 40) {
                for (BlockPos pos : tree) {
                    BlockState state = farmBlockEntity.getLevel().getBlockState(pos);
                    BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
                    if (state.is(BlockTags.LOGS)) {
                        if (farmBlockEntity.getAxe().isEmpty()) {
                            return FarmInteraction.BLOCKED;
                        }

                        if (!farmBlockEntity.handleDrops(state, pos, soil, blockEntity, farmBlockEntity.getAxe())) {
                            return FarmInteraction.BLOCKED;
                        }
                        farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        farmBlockEntity.getAxe()
                            .mineBlock(farmBlockEntity.getLevel(), state, pos, farmBlockEntity.getPlayer());
                    } else if (state.is(BlockTags.LEAVES)) {
                        if (!farmBlockEntity.handleDrops(state, pos, soil, blockEntity, farmBlockEntity.getShears())) {
                            return FarmInteraction.BLOCKED;
                        }
                        farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        farmBlockEntity.getShears()
                            .mineBlock(farmBlockEntity.getLevel(), state, pos, farmBlockEntity.getPlayer());
                    }
                }
                farmBlockEntity.addConsumedPower(-40);
                return FarmInteraction.FINISHED;
            }
            farmBlockEntity
                .addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
            return FarmInteraction.POWERED;
        }
        return FarmInteraction.IGNORED;
    }
}
