package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestNetherWartFarmTask implements FarmTask {
    public static final HarvestNetherWartFarmTask INSTANCE = new HarvestNetherWartFarmTask();

    private HarvestNetherWartFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        BlockPos pos = targetBlock.above();
        BlockState plant = blockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof NetherWartBlock) {
            if (plant.getValue(NetherWartBlock.AGE) >= 3) {
                if (blockEntity.handleDrops(plant, pos, targetBlock, blockEntity, ItemStack.EMPTY)) {
                    blockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    return FarmInteraction.FINISHED;
                }
                return FarmInteraction.BLOCKED;
            }
        }
        return FarmInteraction.IGNORED;
    }
}
