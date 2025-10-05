package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SaplingBlock;

public class PlantSaplingFarmTask implements FarmTask {

    public static final PlantSaplingFarmTask INSTANCE = new PlantSaplingFarmTask();

    private PlantSaplingFarmTask() {
    }

    @Override
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        ItemStack seeds = farmBlockEntity.getSeedsForPos(soil);
        if (seeds.isEmpty() || farmBlockEntity.getLevel().getBlockState(soil).isAir()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SaplingBlock) {
            InteractionResult result = farmBlockEntity.useStack(soil, seeds);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(
                    farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }
        }
        return FarmInteraction.IGNORED;
    }
}
