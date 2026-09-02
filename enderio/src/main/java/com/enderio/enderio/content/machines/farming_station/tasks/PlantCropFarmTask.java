package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlantCropFarmTask implements FarmTask {

    public static final PlantCropFarmTask INSTANCE = new PlantCropFarmTask();

    private PlantCropFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        ItemStack seeds = blockEntity.getSeedsForPos(targetBlock);
        if (seeds.isEmpty() || blockEntity.getLevel().getBlockState(targetBlock).isAir()) {
            return FarmInteraction.IGNORED;
        }

        if (seeds.getItem() instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            if (block instanceof CropBlock || block instanceof StemBlock) {

                // Try plant
                InteractionResult result = blockEntity.useStack(targetBlock, seeds);
                if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                    return FarmInteraction.FINISHED;
                }

                // Try hoe
                ItemStack itemStack = blockEntity.getHoe();
                if (itemStack.isEmpty()) return FarmInteraction.BLOCKED;
                result = blockEntity.useStack(targetBlock, itemStack);
                if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                    return FarmInteraction.FINISHED;
                }

                // Try plant again
                result = blockEntity.useStack(targetBlock, seeds);
                if (result == InteractionResult.FAIL || result == InteractionResult.PASS) {
                    return FarmInteraction.IGNORED;
                }
                return FarmInteraction.FINISHED;
            }

        }
        return FarmInteraction.IGNORED;
    }
}
