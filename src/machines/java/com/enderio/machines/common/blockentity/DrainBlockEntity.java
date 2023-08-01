package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.DrainMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrainBlockEntity extends PoweredMachineBlockEntity {
    public static final String CONSUMED = "consumed";
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.DRAIN_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.DRAIN_USAGE);
    private static final int CAPACITY = 3 * FluidType.BUCKET_VOLUME;
    private static final int ENERGY_PER_BUCKET = 16000; //TODO balance
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private boolean fluidFound = false;
    private int consumed = 0;

    public DrainBlockEntity(BlockEntityType<?> type,
        BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, type, worldPosition, blockState);
        addDataSlot(new FluidStackNetworkDataSlot(getFluidTankNN()::getFluid, getFluidTankNN()::setFluid));

        this.range = 5;

        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r) {
            @Override
            public void updateServerCallback() {
                updateLocations();
            }
        };
        addDataSlot(rangeDataSlot);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .build();
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            drainFluids();
        }

        super.serverTick();
    }

    @Override
    protected boolean isActive() {
        if (!canAct()) {
            return false;
        }
        FluidState fluidState = level.getFluidState(worldPosition.below());
        if (fluidState.isEmpty() || !fluidState.isSource()) {
            return false;
        }
        return getFluidTankNN().fill(new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME;
    }

    public void drainFluids() {
        for (int i = currentIndex; i < Math.min(currentIndex + 3, positions.size()); i++) {
            currentIndex = i;
            BlockPos pos = positions.get(i);

            //Skip, as this is the last checked block
            if (pos.equals(worldPosition.below())) {
                continue;
            }

            //Last block, so reset
            if (currentIndex + 1 == positions.size()) {
                if (!fluidFound) {
                    pos = worldPosition.below(); //No fluids found, so consume the last block under the drain
                }
                currentIndex = 0;
                fluidFound = false;
            }

            //Not a valid fluid
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty() || !fluidState.isSource() || !getFluidTankNN().getFluid().getFluid().isSame(fluidState.getType())) {
                continue;
            }

            //Fluid found, try to consume it
            fluidFound = true;
            if (getFluidTankNN().fill(new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME) {
                if (consumed == ENERGY_PER_BUCKET) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    getFluidTankNN().fill(new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    consumed = 0;
                } else {
                    consumed += getEnergyStorage().consumeEnergy(ENERGY_PER_BUCKET-consumed, false);
                }
            }
        }
    }

    @Override
    public int getMaxRange() {
        return 10;
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get();
    }

    @Override
    public BlockPos getParticleLocation() {
        return worldPosition.below(range + 1);
    }

    @Override
    public void setRange(int range) {
        super.setRange(range);
        updateLocations();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range,-range*2 - 1,-range), worldPosition.offset(range,-1,range))) {
            positions.add(pos.immutable()); //Need to make it immutable
        }
    }

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new MachineFluidTank(CAPACITY, this);
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
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        consumed = pTag.getInt(CONSUMED);
    }
}
