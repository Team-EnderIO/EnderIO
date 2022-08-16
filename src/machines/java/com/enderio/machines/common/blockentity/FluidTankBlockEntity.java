package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.base.common.capability.FluidHandlerBlockItemStack;
import com.enderio.core.common.sync.FluidStackDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.FluidTankMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Bus.MOD)
public abstract class FluidTankBlockEntity extends MachineBlockEntity {
    public enum FluidOperationResult {
        INVALIDFLUIDITEM,
        FAIL,
        SUCCESS
    }

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

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, int capacity) {
        super(type, worldPosition, blockState);

        // Create fluid tank.
        this.fluidTank = createFluidTank(capacity);

        // Create fluid tank storage.
        this.fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);

        // Add capability provider
        addCapabilityProvider(fluidHandler);

        addDataSlot(new FluidStackDataSlot(fluidTank::getFluid, fluidTank::setFluid, SyncMode.WORLD));
    }

    @SubscribeEvent
    public static void blockBroken(final BlockEvent.BreakEvent event) {
        BlockEntity entity = event.getLevel().getBlockEntity(event.getPos());
        if (entity instanceof FluidTankBlockEntity fluidTankBlockEntity) {
            FluidTankBER.removeBlock(fluidTankBlockEntity.getBlockPos());
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

    private IFluidHandlerItem GetIFluidHandlerItem(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        Optional<IFluidHandlerItem> fluidHandlerCap = itemStack
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if (!fluidHandlerCap.isPresent()) {
            return null;
        }

        return fluidHandlerCap.get();
    }

    private void fillInternal() {
        ItemStack inputItem = getInventory().getStackInSlot(0);
        ItemStack outputItem = getInventory().getStackInSlot(1);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET
                        && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    if (this.fillTankFromBucket(filledBucket) == FluidOperationResult.SUCCESS) {
                        inputItem.shrink(1);
                        getInventory().insertItem(1, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                if (outputItem.isEmpty()) {
                    Pair<FluidOperationResult, IFluidHandlerItem> result = fillTankFromItem(inputItem);
                    if (result.getFirst() == FluidOperationResult.SUCCESS) {
                        getInventory().setStackInSlot(1, result.getSecond().getContainer());
                        getInventory().setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public FluidOperationResult fillTankFromBucket(BucketItem bucket) {
        if (bucket == null) {
            return FluidOperationResult.INVALIDFLUIDITEM;
        }

        int filled = fluidTank.fill(new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME),
                IFluidHandler.FluidAction.SIMULATE);
        if (filled != FluidType.BUCKET_VOLUME) {
            return FluidOperationResult.FAIL;
        }

        fluidTank.fill(new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
        return FluidOperationResult.SUCCESS;
    }

    private Pair<FluidOperationResult, IFluidHandlerItem> fillTankFromItem(ItemStack itemStack) {
        IFluidHandlerItem fluidHandler = GetIFluidHandlerItem(itemStack);
        return Pair.of(fluidHandler == null ? FluidOperationResult.INVALIDFLUIDITEM : fillTankFromItem(fluidHandler),
                fluidHandler);
    }

    public FluidOperationResult fillTankFromItem(IFluidHandlerItem item) {
        if (item == null) {
            return FluidOperationResult.INVALIDFLUIDITEM;
        }

        return moveFluids(item, fluidTank, fluidTank.getCapacity()) > 0 ? FluidOperationResult.SUCCESS
                : FluidOperationResult.FAIL;
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
                    Pair<FluidOperationResult, IFluidHandlerItem> result = drainTankWithItem(inputItem);
                    if (result.getFirst() == FluidOperationResult.SUCCESS) {
                        getInventory().setStackInSlot(3, result.getSecond().getContainer());
                        getInventory().setStackInSlot(2, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public Pair<Boolean, FluidStack> drainTankWithBucket() {
        return drainTankWithBucket(null);
    }

    public Pair<Boolean, FluidStack> drainTankWithBucket(Function<FluidStack, Boolean> shouldDrain) {
        if (fluidTank.isEmpty()) {
            return Pair.of(false, null);
        }

        FluidStack stack = fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        if (stack.getAmount() != FluidType.BUCKET_VOLUME) {
            return Pair.of(false, null);
        }

        boolean shouldDrainResult = true;
        if (shouldDrain != null) {
            shouldDrainResult = shouldDrain.apply(stack);
        }
        if (!shouldDrainResult) {
            return Pair.of(false, null);
        }

        fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        return Pair.of(true, stack);
    }

    private Pair<FluidOperationResult, IFluidHandlerItem> drainTankWithItem(ItemStack itemStack) {
        IFluidHandlerItem fluidHandler = GetIFluidHandlerItem(itemStack);
        return Pair.of(fluidHandler == null ? FluidOperationResult.INVALIDFLUIDITEM : drainTankWithItem(fluidHandler),
                fluidHandler);
    }

    public FluidOperationResult drainTankWithItem(IFluidHandlerItem item) {
        if (item == null) {
            return FluidOperationResult.INVALIDFLUIDITEM;
        }

        return moveFluids(fluidTank, item, fluidTank.getFluidAmount()) > 0 ? FluidOperationResult.SUCCESS
                : FluidOperationResult.FAIL;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FluidTankMenu(this, pInventory, pContainerId);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
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
        if (fluidTank.isEmpty()) {
            pTag.remove(FluidHandlerBlockItemStack.FLUID_NBT_KEY);
        } else {
            pTag.put(FluidHandlerBlockItemStack.FLUID_NBT_KEY, fluidTank.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound(FluidHandlerBlockItemStack.FLUID_NBT_KEY));
        FluidTankBER.addBlock(this);
    }

    // endregion
}
