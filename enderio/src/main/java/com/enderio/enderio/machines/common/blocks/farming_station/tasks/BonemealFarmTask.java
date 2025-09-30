package com.enderio.enderio.machines.common.blocks.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BonemealFarmTask implements FarmTask {

    public static final BonemealFarmTask INSTANCE = new BonemealFarmTask();

    private BonemealFarmTask() {
    }

    @Override
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if (bonemealableBlock.isValidBonemealTarget(farmBlockEntity.getLevel(), pos, plant)
                && farmBlockEntity.consumeBonemeal()) {
                if (bonemealableBlock.isBonemealSuccess(farmBlockEntity.getLevel(),
                    farmBlockEntity.getLevel().getRandom(), pos, plant)) {
                    bonemealableBlock.performBonemeal((ServerLevel) farmBlockEntity.getLevel(),
                        farmBlockEntity.getLevel().getRandom(), pos, plant);
                    return FarmInteraction.FINISHED;
                }
            }
        }
        return FarmInteraction.IGNORED;
    }
}
