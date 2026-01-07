package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlantBlockFarmTask implements FarmTask {

    public static final PlantBlockFarmTask INSTANCE = new PlantBlockFarmTask();

    private PlantBlockFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        ItemStack seeds = blockEntity.getSeedsForPos(targetBlock);
        if (seeds.isEmpty() || blockEntity.getLevel().getBlockState(targetBlock).isAir()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            if (block instanceof CactusBlock || block instanceof SugarCaneBlock || block instanceof BambooStalkBlock) {
                InteractionResult result = blockEntity.useStack(targetBlock, seeds);
                if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                    return FarmInteraction.FINISHED;
                }
            }
        }
        return FarmInteraction.IGNORED;
    }
}
