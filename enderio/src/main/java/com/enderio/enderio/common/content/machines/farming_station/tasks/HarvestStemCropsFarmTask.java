package com.enderio.enderio.common.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestStemCropsFarmTask implements FarmTask {

    public static final HarvestStemCropsFarmTask INSTANCE = new HarvestStemCropsFarmTask();

    private HarvestStemCropsFarmTask() {
    }

    @Override
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.is(Blocks.PUMPKIN) || plant.is(Blocks.MELON)) { // TODO I think this is now harder, used to be a block
            // class...
            if (farmBlockEntity.getConsumedPower() >= 40) {
                if (plant.requiresCorrectToolForDrops()) {
                    if (farmBlockEntity.getAxe().isEmpty()) {
                        return FarmInteraction.BLOCKED;
                    }
                }
                if (farmBlockEntity.handleDrops(plant, pos, soil, blockEntity,
                    plant.requiresCorrectToolForDrops() ? farmBlockEntity.getAxe() : ItemStack.EMPTY)) {
                    farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    if (plant.requiresCorrectToolForDrops()) {
                        farmBlockEntity.getAxe()
                            .mineBlock(farmBlockEntity.getLevel(), plant, pos, farmBlockEntity.getPlayer());
                    }
                    farmBlockEntity.addConsumedPower(-40);
                    return FarmInteraction.FINISHED;
                }
                return FarmInteraction.BLOCKED;
            }
            farmBlockEntity
                .addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
            return FarmInteraction.POWERED;
        }
        return FarmInteraction.IGNORED;
    }
}
