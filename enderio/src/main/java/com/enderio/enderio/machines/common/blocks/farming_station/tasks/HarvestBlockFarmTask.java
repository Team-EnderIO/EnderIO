package com.enderio.enderio.machines.common.blocks.farming_station.tasks;

import com.enderio.enderio.api.farm.FarmInteraction;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.farm.FarmingStation;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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
    public FarmInteraction farm(BlockPos soil, FarmingStation farmBlockEntity) {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.getBlock() instanceof CactusBlock || plant.getBlock() instanceof SugarCaneBlock) {
            Optional<BlockPos> top = BlockUtil.getTopConnectedBlock(farmBlockEntity.getLevel(), pos, plant.getBlock(),
                Direction.UP, Blocks.AIR);
            if (top.isPresent() && !top.get().below().equals(pos)) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    for (int i = top.get().below().getY(); i > pos.getY(); i--) {
                        BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
                        if (plant.requiresCorrectToolForDrops()) {
                            if (farmBlockEntity.getAxe().isEmpty()) {
                                return FarmInteraction.BLOCKED;
                            }
                        }
                        if (!farmBlockEntity.handleDrops(plant, blockPos, soil, blockEntity,
                            plant.requiresCorrectToolForDrops() ? farmBlockEntity.getAxe() : ItemStack.EMPTY)) {
                            return FarmInteraction.BLOCKED;
                        }
                        farmBlockEntity.getLevel().setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                        if (plant.requiresCorrectToolForDrops()) {
                            farmBlockEntity.getAxe()
                                .mineBlock(farmBlockEntity.getLevel(), plant, blockPos,
                                    farmBlockEntity.getPlayer());
                        }
                    }
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
