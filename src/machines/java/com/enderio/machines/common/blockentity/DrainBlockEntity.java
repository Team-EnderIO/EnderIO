package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.CodecNetworkDataSlot;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.IFluidTankUser;
import com.enderio.machines.common.attachment.IRangedActor;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.FixedIOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.DrainMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
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

public class DrainBlockEntity extends PoweredMachineBlockEntity implements IRangedActor, IFluidTankUser {
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

    private CodecNetworkDataSlot<ActionRange> actionRangeDataSlot;

    public DrainBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.DRAIN.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        addDataSlot(new FluidStackNetworkDataSlot(() -> TANK.getFluid(this), fluid -> TANK.setFluid(this, fluid)));

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(5, false));
        }

        actionRangeDataSlot = addDataSlot(new CodecNetworkDataSlot<>(this::getActionRange, this::internalSetActionRange, ActionRange.CODEC));
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
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
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
    protected IIOConfig createIOConfig() {
        return new FixedIOConfig(IOMode.PUSH);
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
        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
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
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(CONSUMED, consumed);
        saveTank(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        consumed = pTag.getInt(CONSUMED);
        loadTank(pTag);
    }
}
