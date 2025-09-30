package com.enderio.enderio.machines.common.blocks.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
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
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.getBlock() instanceof NetherWartBlock wart) {
            if (plant.getValue(NetherWartBlock.AGE) >= 3) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    if (farmBlockEntity.handleDrops(plant, pos, soil, blockEntity, ItemStack.EMPTY)) {
                        farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        farmBlockEntity.addConsumedPower(-40);
                        return FarmInteraction.FINISHED;
                    }
                    return FarmInteraction.BLOCKED;
                }
                farmBlockEntity.addConsumedPower(
                    farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }
        }
        return FarmInteraction.IGNORED;
    }
}
