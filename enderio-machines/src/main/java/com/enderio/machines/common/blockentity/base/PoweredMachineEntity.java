package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.energy.MachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A machine that stores power.
 */
public abstract class PoweredMachineEntity extends MachineBlockEntity {
    protected MachineEnergyStorage energyStorage;

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this.energyStorage);

    public PoweredMachineEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        energyStorage = createEnergyStorage();

        // TODO: Dataslot for energy storage?
    }

    protected MachineEnergyStorage createEnergyStorage() {
        // TODO: Need a way of getting the machine's capacitor.
        return new MachineEnergyStorage(Optional::empty) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyCap.cast(); // TODO: FUTURE: Sided access stuff.
        }
        return super.getCapability(cap, side);
    }
}
