package com.enderio.machines.common.blockentity;

import com.enderio.base.common.capability.FluidHandlerBlockItemStack;
import com.enderio.core.common.sync.FluidStackDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER.AnimationInformation;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.util.FluidUtil;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import org.jetbrains.annotations.Nullable;
import java.util.function.Predicate;

public abstract class FluidTankBlockEntity extends MachineBlockEntity {
    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;

        public Standard(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(type, worldPosition, blockState, CAPACITY);
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidType.BUCKET_VOLUME;

        public Enhanced(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(type, worldPosition, blockState, CAPACITY);
        }
    }

    private final FluidTank fluidTank;
    private final MachineFluidHandler fluidHandler;
    private AnimationInformation animationInformation;

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, int capacity) {
        super(type, worldPosition, blockState);

        // Create fluid tank.
        this.fluidTank = createFluidTank(capacity);

        // Create fluid tank storage.
        this.fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);

        // Add capability provider
        addCapabilityProvider(fluidHandler);

        addDataSlot(new FluidStackDataSlot(fluidTank::getFluid, this::setFluidStack, SyncMode.WORLD));
    }

    private void setFluidStack(FluidStack stack) {
        fluidTank.setFluid(stack);

        // This only needs to be done upon initialization.
        if (this.animationInformation == null) {
            this.setAnimationInformation(new AnimationInformation(fluidTank));
        }
    }

    private FluidTank createFluidTank(int capacity) {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.STANDARD;
    }

    @Override
    public void serverTick() {
        if (canActSlow()) {
            fillInternal();
            drainInternal();
        }

        super.serverTick();
    }

    private void fillInternal() {
        ItemStack inputItem = getInventory().getStackInSlot(0);
        ItemStack outputItem = getInventory().getStackInSlot(1);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET
                        && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    if (fillTankFromBucket(filledBucket) > 0) {
                        inputItem.shrink(1);
                        getInventory().insertItem(1, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                if (outputItem.isEmpty()) {
                    Pair<Integer, IFluidHandlerItem> result = fillTankFromItem(inputItem);
                    if (result.getFirst() > 0) {
                        getInventory().setStackInSlot(1, result.getSecond().getContainer());
                        getInventory().setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public int fillTankFromBucket(@Nullable BucketItem bucket) {
        return FluidUtil.fillTankFromBucket(fluidTank, bucket);
    }

    private Pair<Integer, IFluidHandlerItem> fillTankFromItem(ItemStack itemStack) {
        return FluidUtil.fillTankFromItem(fluidTank, itemStack);
    }

    public int fillTankFromItem(@Nullable IFluidHandlerItem item) {
        return FluidUtil.fillTankFromItem(fluidTank, item);
    }

    private void drainInternal() {
        ItemStack inputItem = getInventory().getStackInSlot(2);
        ItemStack outputItem = getInventory().getStackInSlot(3);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                Pair<Boolean, FluidStack> result = drainTankWithBucket(
                        stack -> (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket()
                                && outputItem.getCount() < outputItem.getMaxStackSize())));
                if (result.getFirst()) {
                    inputItem.shrink(1);
                    if (outputItem.isEmpty()) {
                        getInventory().setStackInSlot(3,
                                result.getSecond().getFluid().getBucket().getDefaultInstance());
                    } else {
                        outputItem.grow(1);
                    }
                }
            } else {
                if (outputItem.isEmpty()) {
                    Pair<Integer, IFluidHandlerItem> result = drainTankWithItem(inputItem);
                    if (result.getFirst() > 0) {
                        getInventory().setStackInSlot(3, result.getSecond().getContainer());
                        getInventory().setStackInSlot(2, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public Pair<Boolean, FluidStack> drainTankWithBucket() {
        return FluidUtil.drainTankWithBucket(fluidTank);
    }

    public Pair<Boolean, FluidStack> drainTankWithBucket(@Nullable Predicate<FluidStack> shouldDrain) {
        return FluidUtil.drainTankWithBucket(fluidTank, shouldDrain);
    }

    private Pair<Integer, IFluidHandlerItem> drainTankWithItem(ItemStack itemStack) {
        return FluidUtil.drainTankWithItem(fluidTank, itemStack);
    }

    public int drainTankWithItem(@Nullable IFluidHandlerItem item) {
        return FluidUtil.drainTankWithItem(fluidTank, item);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FluidTankMenu(this, pInventory, pContainerId);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public AnimationInformation getAnimationInformation() {
        return animationInformation;
    }

    public void setAnimationInformation(@Nullable AnimationInformation animationInformation) {
        this.animationInformation = animationInformation;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout
                .builder(false)
                .inputSlot((slot, stack) -> (stack.getItem() instanceof BucketItem bucketItem
                        && bucketItem.getFluid() != Fluids.EMPTY && !(bucketItem instanceof MobBucketItem))
                        || (!(stack.getItem() instanceof BucketItem) && stack
                                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()))
                .outputSlot()
                .inputSlot((slot,
                        stack) -> stack.getItem() == Items.BUCKET || (!(stack.getItem() instanceof BucketItem) && stack
                                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                                .isPresent()))
                .outputSlot()
                .build();
    }

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!fluidTank.isEmpty()) {
            pTag.put(FluidHandlerBlockItemStack.FLUID_NBT_KEY, fluidTank.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound(FluidHandlerBlockItemStack.FLUID_NBT_KEY));
    }

    // endregion
}
