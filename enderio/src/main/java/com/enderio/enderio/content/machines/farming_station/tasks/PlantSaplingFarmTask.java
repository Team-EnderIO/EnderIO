package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class PlantSaplingFarmTask implements FarmTask {

    public static final PlantSaplingFarmTask INSTANCE = new PlantSaplingFarmTask();

    private PlantSaplingFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        ItemResource seeds = blockEntity.getResource(blockEntity.seeds(targetBlock));
        if (seeds.isEmpty() || blockEntity.getLevel().getBlockState(targetBlock).isAir()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SaplingBlock) {
            InteractionResult result = blockEntity.useStack(targetBlock, seeds, blockEntity.seeds(targetBlock));
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                return FarmInteraction.FINISHED;
            }
        }
        return FarmInteraction.IGNORED;
    }
}
