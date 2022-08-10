package com.enderio.machines.common.block;

import java.util.Optional;

import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity.FluidOperationResult;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidTankBlock extends MachineBlock {
    public FluidTankBlock(Properties properties, BlockEntityEntry<? extends MachineBlockEntity> blockEntityType) {
        super(properties, blockEntityType);
    }

    // TODO: Play fluid sound when filling/emptying.
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity) {
            ItemStack itemStack = pPlayer.getItemInHand(pHand);
            if (itemStack.getItem() instanceof BucketItem bucket) {
                // If bucket is empty.
                if (bucket.getFluid() == Fluids.EMPTY) {
                    Pair<Boolean, FluidStack> result = fluidTankBlockEntity.drainTankWithBucket();
                    // If the block entity rejects the bucket, most likely because it's not the same
                    // fluid type.
                    if (!result.getFirst()) {
                        return InteractionResult.FAIL;
                    }

                    // Give the player back a full bucket.
                    ItemStack emptyBucket = result.getSecond().getRawFluid().getBucket().getDefaultInstance();
                    if (itemStack.getCount() == 1) {
                        pPlayer.setItemInHand(pHand, emptyBucket);
                    } else {
                        itemStack.shrink(1);
                        pPlayer.getInventory().add(emptyBucket);
                    }
                    return InteractionResult.SUCCESS;
                    // If bucket has a fluid in it.
                } else {
                    FluidOperationResult result = fluidTankBlockEntity.fillTankFromBucket(bucket);
                    if (result != FluidOperationResult.INVALIDFLUIDITEM) {
                        if (result == FluidOperationResult.FAIL) {
                            return InteractionResult.FAIL;
                        }

                        // Give the player back an empty bucket.
                        pPlayer.setItemInHand(pHand, Items.BUCKET.getDefaultInstance());
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = itemStack
                        .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                if (fluidHandlerCap.isPresent()) {
                    // Read the information for the Fluid in the block.
                    IFluidHandlerItem fluidHandler = fluidHandlerCap.get();
                    FluidStack blockFluidStack = fluidTankBlockEntity.getFluidTank().getFluid();
                    boolean blockIsEmpty = blockFluidStack == null ? true : blockFluidStack.getFluid() == Fluids.EMPTY;

                    // Read the information for the Fluid in the item.
                    boolean itemIsEmpty = false;
                    for (int i = 0; i < fluidHandler.getTanks(); i++) {
                        itemIsEmpty = fluidHandler.getFluidInTank(i).getFluid() == Fluids.EMPTY;
                        if (itemIsEmpty)
                            break;
                    }

                    // If the block and the item are both empty: pass.
                    if (!blockIsEmpty || !itemIsEmpty) {
                        FluidOperationResult result;
                        // If the block is empty, and the item is not empty: fill the block from the
                        // item.
                        if (blockIsEmpty && !itemIsEmpty) {
                            result = fluidTankBlockEntity.fillTankFromItem(fluidHandler);
                            // If the block is not empty, and the item is empty: fill the item from the
                            // block.
                        } else if (!blockIsEmpty && itemIsEmpty) {
                            result = fluidTankBlockEntity.drainTankWithItem(fluidHandler);
                            // If the block is not empty, and the item is not empty:
                        } else {
                            // If the fluid is the same: fill the block from the item.
                            result = fluidTankBlockEntity.fillTankFromItem(fluidHandler);
                        }

                        if (result != FluidOperationResult.INVALIDFLUIDITEM) {
                            return result == FluidOperationResult.FAIL ? InteractionResult.FAIL
                                    : InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
