package com.enderio.machines.common.blockentity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.enderio.machines.common.blockentity.FarmBlockEntity.*;

public interface FarmTask {

    FarmInteraction farm(BlockPos soil, FarmBlockEntity farmBlockEntity);

    FarmTask PLANT_CROP = (soil, farmBlockEntity) -> {
        ItemStack seeds = farmBlockEntity.getSeedForPos(soil);
        if (seeds.isEmpty()) {
            return FarmInteraction.BLOCKED;
        }
        if (seeds.getItem() instanceof BlockItem blockItem && (blockItem.getBlock() instanceof CropBlock || blockItem.getBlock() instanceof StemBlock)) {
            UseOnContext context = new UseOnContext(FARM_PLAYER, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atBottomCenterOf(soil), Direction.UP, soil, false));

            //Try plant
            InteractionResult result = seeds.useOn(context);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                //TODO  consume energy
                return FarmInteraction.USED;
            }

            //Try hoe
            ItemStack itemStack = HOE.getItemStack(farmBlockEntity.getInventory());
            if (itemStack.isEmpty()) {
                return FarmInteraction.BLOCKED;
            }
            result = itemStack.useOn(context);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                //TODO  consume energy
                return FarmInteraction.USED;
            }

            //Try plant again
            result = seeds.useOn(context);
            if (result == InteractionResult.FAIL || result == InteractionResult.PASS) {
                return FarmInteraction.IGNORED;
            }
            return FarmInteraction.USED;
        }
        //TODO  consume energy
        return FarmInteraction.IGNORED;
    };

    FarmTask HARVEST_CROP = (soil, farmBlockEntity) -> {
        BlockPos pos = soil.above();
        BlockState plant = farmBlockEntity.getLevel().getBlockState(pos);
        BlockEntity blockEntity = farmBlockEntity.getLevel().getBlockEntity(pos);
        if (plant.getBlock() instanceof CropBlock crop) {
            if (crop.isMaxAge(plant)) {
                List<ItemStack> drops = Block.getDrops(plant, (ServerLevel) farmBlockEntity.getLevel(), pos, blockEntity, FARM_PLAYER, plant.requiresCorrectToolForDrops() ? AXE.getItemStack(farmBlockEntity.getInventory()) : ItemStack.EMPTY);
                if (plant.requiresCorrectToolForDrops()) {
                    if (AXE.getItemStack(farmBlockEntity.getInventory()).isEmpty()) {
                        return FarmInteraction.BLOCKED;
                    }
                    AXE.getItemStack(farmBlockEntity.getInventory()).mineBlock(farmBlockEntity.getLevel(), plant, pos, FARM_PLAYER);
                }
                farmBlockEntity.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                //TODO  consume energy
                return FarmInteraction.USED;
            }
        }
        return FarmInteraction.IGNORED;
    };

    ArrayList<FarmTask> TASKS = Util.make(() -> {
        ArrayList<FarmTask> list = new ArrayList<>();
        list.add(PLANT_CROP);
        list.add(HARVEST_CROP);
        return list;
    });
}
