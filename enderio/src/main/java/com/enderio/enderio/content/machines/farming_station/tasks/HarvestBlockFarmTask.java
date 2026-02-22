package com.enderio.enderio.content.machines.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class HarvestBlockFarmTask implements FarmTask {

    public static final HarvestBlockFarmTask INSTANCE = new HarvestBlockFarmTask();

    private HarvestBlockFarmTask() {
    }

    @Override
    public <T extends BlockEntity & FarmingMachine> FarmInteraction process(BlockPos targetBlock, T blockEntity) {
        BlockPos pos = targetBlock.above();
        BlockState plant = blockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof CactusBlock || plant.getBlock() instanceof SugarCaneBlock || plant.getBlock() instanceof BambooStalkBlock) {
            Optional<BlockPos> top = BlockUtil.getTopConnectedBlock(blockEntity.getLevel(), pos, plant.getBlock(), Direction.UP, Blocks.AIR);
            if (top.isPresent() && !top.get().below().equals(pos)) {
                for (int i = top.get().below().getY(); i > pos.getY(); i--) {
                    BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
                    if (plant.requiresCorrectToolForDrops()) {
                        if (blockEntity.getAxe().isEmpty()) {
                            return FarmInteraction.BLOCKED;
                        }
                    }
                    if (!blockEntity.handleDrops(plant, blockPos, targetBlock, blockEntity,
                        plant.requiresCorrectToolForDrops() ? blockEntity.getAxe() : ItemStack.EMPTY)) {
                        return FarmInteraction.BLOCKED;
                    }
                    blockEntity.getLevel().setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    if (plant.requiresCorrectToolForDrops()) {
                        blockEntity.getAxe().mineBlock(blockEntity.getLevel(), plant, blockPos, blockEntity.getPlayer());
                    }
                }
                return FarmInteraction.FINISHED;

            }
        }
        return FarmInteraction.IGNORED;
    }
}
