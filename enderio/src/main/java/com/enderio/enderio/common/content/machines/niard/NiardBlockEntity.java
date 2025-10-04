package com.enderio.enderio.common.content.machines.niard;

import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.common.init.EIODataComponents;
import com.enderio.enderio.common.foundation.MachineNBTKeys;
import com.enderio.enderio.common.foundation.attachment.ActionRange;
import com.enderio.enderio.common.foundation.attachment.FluidTankUser;
import com.enderio.enderio.common.foundation.attachment.RangedActor;
import com.enderio.enderio.common.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.common.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.common.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.common.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.common.foundation.state.MachineState;
import com.enderio.enderio.common.content.fluid_tank.InternalTankTasks;
import com.enderio.enderio.common.config.machines.MachinesConfig;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineDataComponents;
import com.enderio.enderio.common.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.common.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.common.foundation.io.fluid.MachineFluidTank;
import com.enderio.enderio.common.foundation.io.fluid.MachineTankLayout;
import com.enderio.enderio.common.foundation.io.fluid.TankAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NiardBlockEntity extends PoweredMachineBlockEntity implements RangedActor, FluidItemInteractive, FluidTankUser {


    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
        MachinesConfig.COMMON.ENERGY.NIARD_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
        MachinesConfig.COMMON.ENERGY.NIARD_USAGE);

    private static final ActionRange DEFAULT_RANGE = new ActionRange(5, false);
    private ActionRange actionRange = DEFAULT_RANGE;

    private NiardRangeIterator iterator;

    public static final  TankAccess TANK = new TankAccess();
    private final MachineFluidHandler fluidHandler;
    private static final int TANK_CAPACITY = 4 * FluidType.BUCKET_VOLUME;

    private static final int ENERGY_PER_BUCKET = 1_500;
    private static final int BASE_IDLE_TICKS = 40;

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();


    public NiardBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.NIARD.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED,
            EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);

        fluidHandler = createFluidHandler();
        iterator = new NiardRangeIterator(worldPosition, actionRange);
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            if (canAct()) {
                fillTank();
            }

            if (canAct((int) (BASE_IDLE_TICKS / getCapacitorData().base()))) {
                tryPlaceFluid();
            }
        }

        super.serverTick();
    }

    private void fillTank() {
        InternalTankTasks.fillInternal(this, TANK, FLUID_FILL_INPUT, FLUID_FILL_OUTPUT);
    }

    private void tryPlaceFluid() {
        int startIndex = iterator.getIndex();

        //check each position in the range and place fluid if possible
        do {
            BlockPos pos = iterator.current();
            if ((isSameLiquid(pos) ? isFlowingBlock(pos) : canPlace(pos))) {
                if (getFluidTank().getFluidAmount() >= FluidType.BUCKET_VOLUME) {
                    placeFluid(pos);
                    consumeResources();
                }
                return;
            }
            iterator.moveToNextPosition();
        } while (iterator.getIndex() != startIndex);
    }

    private void placeFluid(BlockPos pos) {
        Fluid fluid = TANK.getFluid(this).getFluid();
        if (fluid == Fluids.EMPTY) return;

        BlockState fluidBlockState = fluid.defaultFluidState().createLegacyBlock();
        level.setBlock(pos, fluidBlockState, Block.UPDATE_ALL);
    }

    private void consumeResources() {
        TANK.drain(this, FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        getEnergyStorage().consumeEnergy(ENERGY_PER_BUCKET);
    }

    @Override
    public void clientTick() {
        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                MachinesConfig.CLIENT.BLOCKS.NIARD_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    @Override
    public boolean isActive() {
        return hasEnergy();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return new MachineTankLayout.Builder().tank(TANK, TANK_CAPACITY).build();
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .inputSlot((slot, stack) -> acceptItemDrain(stack))
            .slotAccess(FLUID_FILL_INPUT)
            .outputSlot()
            .slotAccess(FLUID_FILL_OUTPUT)
            .build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }

    @Override
    public int getMaxRange() {
        return 7;
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        iterator = new NiardRangeIterator(getBlockPos(), getActionRange());

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new NiardMenu(containerId, playerInventory, this);
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    private BlockPos getParticleLocation() {
        return worldPosition.below(getRange() + 1);
    }

    private boolean canPlace(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        boolean isAir = state.isAir();
        boolean isLiquid = !state.getFluidState().isEmpty();
        boolean replaceable = state.canBeReplaced();

        return isAir || (!isLiquid && replaceable);
    }

    private boolean isSameLiquid(@Nonnull BlockPos pos) {
        Fluid fluidAtPos = level.getBlockState(pos).getFluidState().getType();
        Fluid fluidInTank = TANK.getFluid(this).getFluid();

        return (fluidInTank == Fluids.WATER && (fluidAtPos == Fluids.WATER || fluidAtPos == Fluids.FLOWING_WATER))
            || (fluidInTank == Fluids.LAVA && (fluidAtPos == Fluids.LAVA  || fluidAtPos == Fluids.FLOWING_LAVA))
            || fluidInTank.isSame(fluidAtPos);
    }

    private boolean isFlowingBlock(@Nonnull BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        return !fluidState.isSource();
    }

    public boolean acceptItemDrain(ItemStack item) {
        IFluidHandlerItem fluidHandlerCap = item.getCapability(Capabilities.FluidHandler.ITEM);
        return fluidHandlerCap != null;
    }

    // region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            var tank = TANK.getTank(this);
            tank.setFluid(storedFluid.copy());
        }

        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var tank = TANK.getTank(this);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank.getFluid()));
        }

        components.set(MachineDataComponents.ACTION_RANGE, actionRange);
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);
        saveTank(registries, tag);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            tag.put(MachineNBTKeys.ACTION_RANGE, actionRange.save(registries));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        loadTank(lookupProvider, tag);

        if (tag.contains(MachineNBTKeys.ACTION_RANGE)) {
            actionRange = ActionRange.parse(lookupProvider, Objects.requireNonNull(tag.get(MachineNBTKeys.ACTION_RANGE)));
        } else {
            actionRange = new ActionRange(getMaxRange(), false);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.ACTION_RANGE);
    }

    // endregion
}
