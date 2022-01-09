package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.sync.FluidStackDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.fluid.FluidTankMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.menu.FluidTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class FluidTankBlockEntity extends MachineBlockEntity {

    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidAttributes.BUCKET_VOLUME;

        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(pType, pWorldPosition, pBlockState, CAPACITY);
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidAttributes.BUCKET_VOLUME;

        public Enhanced(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(pType, pWorldPosition, pBlockState, CAPACITY);
        }
    }

    private final FluidTankMaster fluidTank;

    public FluidTankBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int capacity) {
        super(MachineTier.STANDARD, pType, pWorldPosition, pBlockState);
        this.fluidTank =  new FluidTankMaster(capacity, getIoConfig());

        addDataSlot(new FluidStackDataSlot(() -> fluidTank.getFluidInTank(0), fluidTank::setFluid, SyncMode.WORLD));
        getItemHandler().addPredicate(0, itemStack ->
            (itemStack.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid() != Fluids.EMPTY && !(bucketItem instanceof MobBucketItem))
                || (!(itemStack.getItem() instanceof BucketItem) && itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()));
        getItemHandler().addPredicate(2, itemStack ->
            itemStack.getItem() == Items.BUCKET
            || (!(itemStack.getItem() instanceof BucketItem) && itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()));
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        // TODO: Rename to fluid or tank.
        // TODO: Common place for all NBT names.
        pTag.put("Fluids", fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("Fluids"));
    }

    @Override
    public void tick() {
        if (shouldActSlow()) {
            fillInternal();
            drainInternal();
        }
        super.tick();
    }

    private void fillInternal() {
        ItemStack inputItem = getItemHandler().getStackInSlot(0);
        ItemStack outputItem = getItemHandler().getStackInSlot(1);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    int filled = fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == FluidAttributes.BUCKET_VOLUME) {
                        fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        getItemHandler().forceInsertItem(1, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();

                    int filled = moveFluids(itemFluid, fluidTank, fluidTank.getCapacity());
                    if (filled > 0) {
                        getItemHandler().setStackInSlot(1, itemFluid.getContainer());
                        getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private void drainInternal() {
        ItemStack inputItem = getItemHandler().getStackInSlot(2);
        ItemStack outputItem = getItemHandler().getStackInSlot(3);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (!fluidTank.isEmpty()) {
                    FluidStack stack = fluidTank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    if (stack.getAmount() == FluidAttributes.BUCKET_VOLUME && (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket() && outputItem.getCount() < outputItem.getMaxStackSize()))) {
                        fluidTank.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            getItemHandler().setStackInSlot(3, stack.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();
                    int filled = moveFluids(fluidTank, itemFluid, fluidTank.getFluidAmount());
                    if (filled > 0) {
                        getItemHandler().setStackInSlot(3, itemFluid.getContainer());
                        getItemHandler().setStackInSlot(2, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (direction != null) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return LazyOptional.of(() -> fluidTank.getAccess(direction)).cast();
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return LazyOptional.of(() -> getItemHandler().getAccess(direction)).cast();
        }
        return super.getCapability(cap, direction);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> fluidTank).cast();
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(this::getItemHandler).cast();
        return super.getCapability(cap);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FluidTankMenu(this, pInventory, pContainerId);
    }

    public FluidTankMaster getFluidTank() {
        return fluidTank;
    }

    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        return Optional.ofNullable(ItemSlotLayout
            .builder()
            .addSlot(0, ItemSlotLayout.SlotType.INPUT)
            .addSlot(2, ItemSlotLayout.SlotType.INPUT)
            .addSlot(1, ItemSlotLayout.SlotType.OUTPUT)
            .addSlot(3, ItemSlotLayout.SlotType.OUTPUT)
            .build());
    }

    @Override
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        return new ItemHandlerMaster(getIoConfig(), layout);
    }
}
