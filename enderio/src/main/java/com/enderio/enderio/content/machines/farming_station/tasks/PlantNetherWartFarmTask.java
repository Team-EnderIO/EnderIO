package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlantNetherWartFarmTask implements FarmTask {

    public static final PlantNetherWartFarmTask INSTANCE = new PlantNetherWartFarmTask();

    private PlantNetherWartFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        ItemStack seeds = blockEntity.getSeedsForPos(targetBlock);
        if (seeds.isEmpty() || blockEntity.getLevel().getBlockState(targetBlock).isAir()) {
            return FarmInteraction.IGNORED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof NetherWartBlock) {
            InteractionResult result = blockEntity.useStack(targetBlock, seeds);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                return FarmInteraction.FINISHED;
            }
        }
        return FarmInteraction.IGNORED;
    }
}
