package com.enderio.api.farm;

import net.minecraft.BlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FarmTask {

    FarmInteraction farm(BlockPos soil, IFarmingStation farmBlockEntity);

    FarmTask PLANT_CROP = (soil, farmBlockEntity) -> {
        ItemStack seeds = farmBlockEntity.getSeedsForPos(soil);
        if (seeds.isEmpty()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof CropBlock || blockItem.getBlock() instanceof StemBlock)) {

            //Try plant
            InteractionResult result = farmBlockEntity.useStack(soil, seeds);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }

            //Try hoe
            ItemStack itemStack = farmBlockEntity.getHoe();
            if (itemStack.isEmpty()) {
                return FarmInteraction.BLOCKED;
            }

            result = farmBlockEntity.useStack(soil, itemStack);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }

            //Try plant again
            result = farmBlockEntity.useStack(soil, seeds);
            if (result == InteractionResult.FAIL || result == InteractionResult.PASS) {
                return FarmInteraction.IGNORED;
            }
            return FarmInteraction.FINISHED;
        }
        return FarmInteraction.IGNORED;
    };

    FarmTask PLANT_BLOCK = (soil, farmBlockEntity) -> {
        ItemStack seeds = farmBlockEntity.getSeedsForPos(soil);
        if (seeds.isEmpty()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof CactusBlock || blockItem.getBlock() instanceof SugarCaneBlock)) {
            InteractionResult result = farmBlockEntity.useStack(soil, seeds);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }
        }
        return FarmInteraction.IGNORED;
    };

    FarmTask BONEMEAL = (soil, farmBlockEntity) -> {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        if (plant.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if (bonemealableBlock.isValidBonemealTarget(farmBlockEntity.getLevel(), pos, plant) && farmBlockEntity.consumeBonemeal()) {
                if (bonemealableBlock.isBonemealSuccess(farmBlockEntity.getLevel(), farmBlockEntity.getLevel().getRandom(), pos, plant)) {
                    bonemealableBlock.performBonemeal((ServerLevel) farmBlockEntity.getLevel(), farmBlockEntity.getLevel().getRandom(), pos, plant);
                    return FarmInteraction.FINISHED;
                }
            }
        }
        return FarmInteraction.IGNORED;
    };

    FarmTask HARVEST_CROP = (soil, farmBlockEntity) -> {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.getBlock() instanceof CropBlock crop) {
            if (crop.isMaxAge(plant)) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    if (plant.requiresCorrectToolForDrops()) {
                        if (farmBlockEntity.getAxe().isEmpty()) {
                            return FarmInteraction.BLOCKED;
                        }
                        farmBlockEntity.getAxe().mineBlock(farmBlockEntity.getLevel(), plant, pos, farmBlockEntity.getPlayer());
                    }
                    farmBlockEntity.handleDrops(plant, pos, soil, blockEntity, plant.requiresCorrectToolForDrops() ? farmBlockEntity.getAxe() : ItemStack.EMPTY);
                    farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }
        }
        return FarmInteraction.IGNORED;
    };

    FarmTask HARVEST_STEM_CROPS = (soil, farmBlockEntity) -> {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.is(Blocks.PUMPKIN) || plant.is(Blocks.PUMPKIN)) { //TODO I think this is now harder, used to be a block class...
            if (farmBlockEntity.getConsumedPower() >= 40) {
                farmBlockEntity.addConsumedPower(-40);
                if (plant.requiresCorrectToolForDrops()) {
                    if (farmBlockEntity.getAxe().isEmpty()) {
                        return FarmInteraction.BLOCKED;
                    }
                    farmBlockEntity.getAxe().mineBlock(farmBlockEntity.getLevel(), plant, pos, farmBlockEntity.getPlayer());
                }
                farmBlockEntity.handleDrops(plant, pos, soil, blockEntity, plant.requiresCorrectToolForDrops() ? farmBlockEntity.getAxe() : ItemStack.EMPTY);
                farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                return FarmInteraction.FINISHED;
            }
            farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
            return FarmInteraction.POWERED;
        }
        return FarmInteraction.IGNORED;
    };

    FarmTask HARVEST_BLOCK = (soil, farmBlockEntity) -> {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.getBlock() instanceof CactusBlock || plant.getBlock() instanceof SugarCaneBlock) {
            Optional<BlockPos> top = BlockUtil.getTopConnectedBlock(farmBlockEntity.getLevel(), pos, plant.getBlock(), Direction.UP, Blocks.AIR);
            if (top.isPresent() && !top.get().below().equals(pos)) {
                if (farmBlockEntity.getConsumedPower() >= 40) {
                    farmBlockEntity.addConsumedPower(-40);
                    for (int i = top.get().below().getY(); i > pos.getY(); i--) {
                        BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
                        if (plant.requiresCorrectToolForDrops()) {
                            if (farmBlockEntity.getAxe().isEmpty()) {
                                return FarmInteraction.BLOCKED;
                            }
                            farmBlockEntity.getAxe().mineBlock(farmBlockEntity.getLevel(), plant, blockPos, farmBlockEntity.getPlayer());
                        }
                        farmBlockEntity.handleDrops(plant, pos, soil, blockEntity, plant.requiresCorrectToolForDrops() ? farmBlockEntity.getAxe() : ItemStack.EMPTY);
                        farmBlockEntity.getLevel().setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    }
                    return FarmInteraction.FINISHED;
                }
                farmBlockEntity.addConsumedPower(farmBlockEntity.consumeEnergy(40 - farmBlockEntity.getConsumedPower(), false));
                return FarmInteraction.POWERED;
            }
        }
        return FarmInteraction.IGNORED;
    };

    //TODO Move to api file
    ArrayList<FarmTask> TASKS = Util.make(() -> {
        ArrayList<FarmTask> list = new ArrayList<>();
        list.add(PLANT_CROP);
        list.add(PLANT_BLOCK);
        list.add(BONEMEAL);
        list.add(HARVEST_CROP);
        list.add(HARVEST_STEM_CROPS);
        list.add(HARVEST_BLOCK);
        return list;
    });
}
