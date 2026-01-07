package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestFlower implements FarmTask {

    public static final HarvestFlower INSTANCE = new HarvestFlower();

    private HarvestFlower() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        BlockPos pos = targetBlock.above();
        BlockState plant = blockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof FlowerBlock) {
            if (plant.requiresCorrectToolForDrops()) {
                if (blockEntity.getAxe().isEmpty()) {
                    return FarmInteraction.BLOCKED;
                }
            }
            if (blockEntity.handleDrops(plant, pos, targetBlock, blockEntity, plant.requiresCorrectToolForDrops() ? blockEntity.getAxe() : ItemStack.EMPTY)) {
                blockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                if (plant.requiresCorrectToolForDrops()) {
                    blockEntity.getAxe().mineBlock(blockEntity.getLevel(), plant, pos, blockEntity.getPlayer());
                }
                return FarmInteraction.FINISHED;
            }
            return FarmInteraction.BLOCKED;

        }
        return FarmInteraction.IGNORED;
    }
}
