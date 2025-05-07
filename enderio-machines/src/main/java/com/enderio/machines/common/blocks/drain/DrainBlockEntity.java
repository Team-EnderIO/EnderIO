package com.enderio.machines.common.blocks.drain;

import com.enderio.base.api.UseOnly;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.IOMode;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
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
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrainBlockEntity extends PoweredMachineBlockEntity implements RangedActor, FluidTankUser {
    public static final String CONSUMED = "Consumed";
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.DRAIN_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.DRAIN_USAGE);

    private static final ActionRange DEFAULT_RANGE = new ActionRange(5, false);

    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    private static final int CAPACITY = 3 * FluidType.BUCKET_VOLUME;
    private static final int ENERGY_PER_BUCKET = 1_500;
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private boolean fluidFound = false;
    private int consumed = 0;
    private Fluid type = Fluids.EMPTY;

    private ActionRange actionRange = DEFAULT_RANGE;

    public DrainBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.DRAIN.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
        fluidHandler = createFluidHandler();
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    @UseOnly(LogicalSide.SERVER)
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        updateLocations();
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getMaxRange() {
        return 10;
    }

    @Override
    public @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
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
    public boolean isActive() {
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
        return TANK.fill(this, new FluidStack(type, FluidType.BUCKET_VOLUME),
                IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME;
    }

    public void drainFluids() {
        int stop = Math.min(currentIndex + getRange(), positions.size());
        while (currentIndex < stop) {
            if (currentIndex >= positions.size()) {
                currentIndex--;
            }
            BlockPos pos = positions.get(currentIndex);

            // Skip, as this is the last checked block
            if (pos.equals(worldPosition.below()) && positions.size() != 1) {
                currentIndex++;
                continue;
            }

            // Last block, so reset
            if (currentIndex + 1 == positions.size()) {
                if (!fluidFound) {
                    pos = worldPosition.below(); // No fluids found, so consume the last block under the drain
                } else {
                    currentIndex = 0;
                    fluidFound = false;
                }
            }

            // Not a valid fluid
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty() || !fluidState.isSource()
                    || !TANK.isFluidValid(this, new FluidStack(fluidState.getType(), 1))) {
                currentIndex++;
                continue;
            }

            // Fluid found, try to consume it
            fluidFound = true;
            if (TANK.fill(this, new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME),
                    IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
                if (consumed >= ENERGY_PER_BUCKET) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    TANK.fill(this, new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME),
                            IFluidHandler.FluidAction.EXECUTE);
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
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                    MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get());
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
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range, -range * 2 - 1, -range),
                worldPosition.offset(range, -1, range))) {
            positions.add(pos.immutable()); // Need to make it immutable
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DrainMenu(containerId, playerInventory, this);
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        pTag.putInt(CONSUMED, consumed);
        saveTank(lookupProvider, pTag);
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            tag.put(MachineNBTKeys.ACTION_RANGE, actionRange.save(registries));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        consumed = pTag.getInt(CONSUMED);
        loadTank(lookupProvider, pTag);

        // TODO: Ender IO 8 - remove support for old attachment loading
        if (hasData(MachineAttachments.ACTION_RANGE)) {
            actionRange = getData(MachineAttachments.ACTION_RANGE);
            removeData(MachineAttachments.ACTION_RANGE);
        } else if (pTag.contains(MachineNBTKeys.ACTION_RANGE)) {
            actionRange = ActionRange.parse(lookupProvider,
                    Objects.requireNonNull(pTag.get(MachineNBTKeys.ACTION_RANGE)));
        } else {
            actionRange = DEFAULT_RANGE;
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            var tank = TANK.getTank(this);
            tank.setFluid(storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        // Only if unchanged.
        if (!actionRange.equals(DEFAULT_RANGE)) {
            components.set(MachineDataComponents.ACTION_RANGE, actionRange);
        }

        var tank = TANK.getTank(this);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank.getFluid()));
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.ACTION_RANGE);
        tag.remove(CONSUMED);
    }
}
