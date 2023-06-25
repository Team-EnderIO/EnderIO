package com.enderio.machines.common.blockentity;

import com.enderio.core.common.sync.FluidStackDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.recipe.TankRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class FluidTankBlockEntity extends MachineBlockEntity {

    public static final String FLUID_TAG_KEY = "fluid";

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

    private final TankRecipe.Container container;
    private final MachineFluidTank fluidTank;
    private Optional<TankRecipe> currentRecipe = Optional.empty();

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_OUTPUT = new SingleSlotAccess();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, int capacity) {
        super(type, worldPosition, blockState);

        // Create fluid tank.
        this.fluidTank = new MachineFluidTank(capacity, this) {
            @Override
            protected void onContentsChanged() {
                onTankContentsChanged();
                super.onContentsChanged();
            }
        };

        // Create fluid tank storage.
        MachineFluidHandler fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);

        // Add capability provider
        addCapabilityProvider(fluidHandler);
        addDataSlot(new FluidStackDataSlot(fluidTank::getFluid, fluidTank::setFluid, SyncMode.WORLD));

        // Wrap container for fluid recipes
        container = new TankRecipe.Container(getInventory(), fluidTank);
    }

    @Override
    public void serverTick() {
        if (canActSlow()) {
            fillInternal();
            drainInternal();
            tryTankRecipe();
        }

        super.serverTick();
    }

    //TODO: enable fluid tanks to receive stackable fluid containers
    private void fillInternal() {
        ItemStack inputItem = FLUID_FILL_INPUT.getItemStack(this);
        ItemStack outputItem = FLUID_FILL_OUTPUT.getItemStack(this);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET && outputItem.getCount() < outputItem.getMaxStackSize())) {
                    int filled = fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                    if (filled == FluidType.BUCKET_VOLUME) {
                        fluidTank.fill(new FluidStack(filledBucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        FLUID_FILL_OUTPUT.insertItem(this, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();

                    int filled = moveFluids(itemFluid, fluidTank, fluidTank.getCapacity());
                    if (filled > 0) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, itemFluid.getContainer());
                        FLUID_FILL_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return fluidTank.onClickedWithPotentialFluidItem(player, hand);
    }

    //TODO: enable fluid tanks to receive stackable fluid containers
    private void drainInternal() {
        ItemStack inputItem = FLUID_DRAIN_INPUT.getItemStack(this);
        ItemStack outputItem = FLUID_DRAIN_OUTPUT.getItemStack(this);
        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (!fluidTank.isEmpty()) {
                    FluidStack stack = fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    if (stack.getAmount() == FluidType.BUCKET_VOLUME && (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket()
                        && outputItem.getCount() < outputItem.getMaxStackSize()))) {
                        fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            FLUID_DRAIN_OUTPUT.setStackInSlot(this, stack.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                Optional<IFluidHandlerItem> fluidHandlerCap = inputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (fluidHandlerCap.isPresent() && outputItem.isEmpty()) {
                    IFluidHandlerItem itemFluid = fluidHandlerCap.get();
                    int filled = moveFluids(fluidTank, itemFluid, fluidTank.getFluidAmount());
                    if (filled > 0) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, itemFluid.getContainer());
                        FLUID_DRAIN_INPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private void tryTankRecipe() {
        currentRecipe.ifPresent(recipe -> {
            if (recipe.isEmptying()) {
                ItemStack outputStack = FLUID_FILL_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipe.getOutput()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_FILL_INPUT.getItemStack(this).shrink(1);

                    fluidTank.fill(recipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, new ItemStack(recipe.getOutput(), 1));
                    } else {
                        FLUID_FILL_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            } else {
                ItemStack outputStack = FLUID_DRAIN_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipe.getOutput()) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_DRAIN_INPUT.getItemStack(this).shrink(1);

                    fluidTank.drain(recipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, new ItemStack(recipe.getOutput(), 1));
                    } else {
                        FLUID_DRAIN_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
        });
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        if (level != null) {
            if (!level.isClientSide()) {
                currentRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.TANK.type().get(), container, level);
            }
        }
    }

    private void onTankContentsChanged() {
        if (level != null) {
            if (!level.isClientSide()) {
                currentRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.TANK.type().get(), container, level);
            }
        }
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
            .builder()
            .inputSlot()
            .slotAccess(FLUID_FILL_INPUT)
            .outputSlot()
            .slotAccess(FLUID_FILL_OUTPUT)
            .inputSlot()
            .slotAccess(FLUID_DRAIN_INPUT)
            .outputSlot()
            .slotAccess(FLUID_DRAIN_OUTPUT)
            .build();
    }

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(FLUID_TAG_KEY, fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("fluid"));
    }

    // endregion
}
