package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.IOMode;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.DrainMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrainBlockEntity extends PoweredMachineBlockEntity implements RangedActor, FluidTankUser {
    public static final String CONSUMED = "Consumed";
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.DRAIN_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.DRAIN_USAGE);
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    private static final int CAPACITY = 3 * FluidType.BUCKET_VOLUME;
    private static final int ENERGY_PER_BUCKET = 1_500;
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private boolean fluidFound = false;
    private int consumed = 0;
    private Fluid type = Fluids.EMPTY;

    private final NetworkDataSlot<ActionRange> actionRangeDataSlot;

    public DrainBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.DRAIN.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        addDataSlot(NetworkDataSlot.FLUID_STACK.create(() -> TANK.getFluid(this), fluid -> TANK.setFluid(this, fluid)));

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(5, false));
        }

        actionRangeDataSlot = addDataSlot(ActionRange.DATA_SLOT_TYPE.create(this::getActionRange, this::internalSetActionRange));
    }

    @Override
    public int getMaxRange() {
        return 10;
    }

    @Override
    public ActionRange getActionRange() {
        return getData(MachineAttachments.ACTION_RANGE);
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(actionRangeDataSlot, actionRange);
        } else {
            internalSetActionRange(actionRange);
        }
    }

    private void internalSetActionRange(ActionRange actionRange) {
        setData(MachineAttachments.ACTION_RANGE, actionRange);
        updateLocations();
        setChanged();
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .build();
    }

    @Override
    public @Nullable MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, CAPACITY, f -> type.isSame(f.getFluid())).build();
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
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
                super.onContentsChanged(slot);
                updateMachineState(MachineState.FULL_TANK, TANK.getFluidAmount(this) >= TANK.getCapacity(this));
            }
        };
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            drainFluids();
        }

        super.serverTick();
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PUSH);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
    }

    @Override
    protected boolean isActive() {
        if (!canAct()) {
            return false;
        }

        FluidState fluidState = level.getFluidState(worldPosition.below());
        if (fluidState.isEmpty() || !fluidState.isSource()) {
            updateMachineState(MachineState.NO_SOURCE, true);
            return false;
        }
        updateMachineState(MachineState.NO_SOURCE, false);
        type = fluidState.getType();
        return TANK.fill(this, new FluidStack(type, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME;
    }

    public void drainFluids() {
        int stop = Math.min(currentIndex + getRange(), positions.size());
        while (currentIndex < stop) {
            if (currentIndex >= positions.size()) {
                currentIndex--;
            }
            BlockPos pos = positions.get(currentIndex);

            //Skip, as this is the last checked block
            if (pos.equals(worldPosition.below()) && positions.size() != 1) {
                currentIndex++;
                continue;
            }

            //Last block, so reset
            if (currentIndex + 1 == positions.size()) {
                if (!fluidFound) {
                    pos = worldPosition.below(); //No fluids found, so consume the last block under the drain
                } else {
                    currentIndex = 0;
                    fluidFound = false;
                }
            }

            //Not a valid fluid
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty() || !fluidState.isSource() || !TANK.isFluidValid(this, new FluidStack(fluidState.getType(), 1))) {
                currentIndex++;
                continue;
            }

            //Fluid found, try to consume it
            fluidFound = true;
            if (TANK.fill(this, new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
                if (consumed >= ENERGY_PER_BUCKET) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    TANK.fill(this, new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    consumed -= ENERGY_PER_BUCKET;
                    currentIndex++;
                } else {
                    consumed += getEnergyStorage().consumeEnergy(ENERGY_PER_BUCKET - consumed, false);
                }
                return;
            }
        }
    }

    public BlockPos getParticleLocation() {
        return worldPosition.below(getRange() + 1);
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(), MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        currentIndex = 0;
        int range = getRange();
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range,-range*2 - 1,-range), worldPosition.offset(range,-1,range))) {
            positions.add(pos.immutable()); //Need to make it immutable
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DrainMenu(this, playerInventory, containerId);
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        pTag.putInt(CONSUMED, consumed);
        saveTank(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        consumed = pTag.getInt(CONSUMED);
        loadTank(lookupProvider, pTag);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            setData(MachineAttachments.ACTION_RANGE, actionRange);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(MachineDataComponents.ACTION_RANGE, getData(MachineAttachments.ACTION_RANGE));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        removeData(MachineAttachments.ACTION_RANGE);
    }
}
