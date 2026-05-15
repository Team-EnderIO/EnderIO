package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class PlantCropFarmTask implements FarmTask {

    public static final PlantCropFarmTask INSTANCE = new PlantCropFarmTask();

    private PlantCropFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        ItemResource seeds = blockEntity.getResource(blockEntity.seeds(targetBlock));
        if (seeds.isEmpty() || blockEntity.getLevel().getBlockState(targetBlock).isAir()) {
            return FarmInteraction.BLOCKED;
        }

        if (seeds.getItem() instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            if (block instanceof CropBlock || block instanceof StemBlock) {

                // Try plant
                InteractionResult result = blockEntity.useStack(targetBlock, seeds, blockEntity.seeds(targetBlock));
                if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                    return FarmInteraction.FINISHED;
                }

                // Try hoe
                ItemResource hoe = blockEntity.getResource(blockEntity.hoe());
                if (hoe.isEmpty()) return FarmInteraction.BLOCKED;
                result = blockEntity.useStack(targetBlock, hoe, blockEntity.hoe());
                if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                    return FarmInteraction.FINISHED;
                }

                // Try plant again
                result = blockEntity.useStack(targetBlock, seeds, blockEntity.seeds(targetBlock));
                if (result == InteractionResult.FAIL || result == InteractionResult.PASS) {
                    return FarmInteraction.IGNORED;
                }
                return FarmInteraction.FINISHED;
            }

        }
        return FarmInteraction.IGNORED;
    }
}
