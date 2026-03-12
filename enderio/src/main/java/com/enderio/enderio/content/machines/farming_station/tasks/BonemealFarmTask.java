package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BonemealFarmTask implements FarmTask {

    public static final BonemealFarmTask INSTANCE = new BonemealFarmTask();

    private BonemealFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        BlockPos pos = targetBlock.above();
        BlockState plant = blockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if (bonemealableBlock.isValidBonemealTarget(blockEntity.getLevel(), pos, plant, false)
                && blockEntity.consumeBonemeal()) {
                if (bonemealableBlock.isBonemealSuccess(blockEntity.getLevel(),
                    blockEntity.getLevel().getRandom(), pos, plant)) {
                    bonemealableBlock.performBonemeal((ServerLevel) blockEntity.getLevel(),
                        blockEntity.getLevel().getRandom(), pos, plant);
                    return FarmInteraction.FINISHED;
                }
            }
        }
        return FarmInteraction.IGNORED;
    }
}
