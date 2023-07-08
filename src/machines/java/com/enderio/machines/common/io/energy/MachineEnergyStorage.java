package com.enderio.machines.common.io.energy;

import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.MachineNBTKeys;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.Supplier;

/**
 * Machine energy storage medium.
 * Uses capacitor keys to determine maximum capacity and transfer rate.
 * Also provides sided access through capabilities.
 */
public class MachineEnergyStorage implements IMachineEnergyStorage, IEnderCapabilityProvider<IEnergyStorage>, INBTSerializable<CompoundTag> {
    private final IIOConfig config;
    private final EnergyIOMode ioMode;

    private int energyStored;

    private final Supplier<Integer> capacity, usageRate;

    private final EnumMap<Direction, LazyOptional<Sided>> sideCache = new EnumMap<>(Direction.class);
    private LazyOptional<MachineEnergyStorage> selfCache = LazyOptional.empty();

    public MachineEnergyStorage(IIOConfig config, EnergyIOMode ioMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        this.config = config;
        this.ioMode = ioMode;
        this.capacity = capacity;
        this.usageRate = usageRate;
    }

    @Override
    public final IIOConfig getConfig() {
        return config;
    }

    @Override
    public final EnergyIOMode getIOMode() {
        return ioMode;
    }

    // Override in BE
    protected void onContentsChanged() {

    }

    @Override
    public int getEnergyStored() {
        return Math.min(energyStored, getMaxEnergyStored());
    }

    /**
     * Set the energy stored in the storage.
     */
    public void setEnergyStored(int energy) {
        energyStored = Math.min(energy, getMaxEnergyStored());
    }

    @Override
    public int addEnergy(int energy) {
        int energyBefore = energyStored;
        energyStored = Math.min(energyStored + energy, getMaxEnergyStored());
        onContentsChanged();
        return energyStored - energyBefore;
    }

    @Override
    public int takeEnergy(int energy) {
        int energyBefore = energyStored;
        energyStored = Math.max(energyStored - energy, 0);
        onContentsChanged();
        return energyBefore - energyStored;
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        // Cap rate
        int usableEnergy = Math.min(energy, getMaxEnergyUse());
        if (!simulate) {
            return takeEnergy(usableEnergy);
        }
        return usableEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity.get();
    }

    @Override
    public int getMaxEnergyUse() {
        return usageRate.get();
    }

    @Override
    public boolean canExtract() {
        return ioMode.canOutput();
    }

    @Override
    public boolean canReceive() {
        return ioMode.canInput();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(getMaxEnergyUse() * 2, maxReceive));
        if (!simulate) {
            addEnergy(energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        int energyExtracted = Math.min(getEnergyStored(), maxExtract);
        if (!simulate) {
            takeEnergy(energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public Capability<IEnergyStorage> getCapabilityType() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public LazyOptional<IEnergyStorage> getCapability(@Nullable Direction side) {
        if (side == null) {
            // Create own cache if its been invalidated or not created yet.
            if (!selfCache.isPresent())
                selfCache = LazyOptional.of(() -> this);
            return selfCache.cast();
        }

        if (!config.getMode(side).canConnect())
            return LazyOptional.empty();
        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Sided(this, dir))).cast();
    }

    @Override
    public void invalidateSide(@Nullable Direction side) {
        if (side != null) {
            if (sideCache.containsKey(side)) {
                sideCache.get(side).invalidate();
                sideCache.remove(side);
            }
        } else {
            selfCache.invalidate();
        }
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Sided> side : sideCache.values()) {
            side.invalidate();
        }
        selfCache.invalidate();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(MachineNBTKeys.ENERGY_STORED, energyStored);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energyStored = nbt.getInt(MachineNBTKeys.ENERGY_STORED);
    }

    private static class Sided implements IEnergyStorage {

        private final MachineEnergyStorage wrapped;
        private final Direction side;

        public Sided(MachineEnergyStorage wrapped, Direction side) {
            this.wrapped = wrapped;
            this.side = side;
        }

        @Override
        public int getEnergyStored() {
            return wrapped.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return wrapped.getMaxEnergyStored();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive())
                return 0;
            return wrapped.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtract())
                return 0;
            return wrapped.extractEnergy(maxExtract, simulate);
        }

        @Override
        public boolean canExtract() {
            if (wrapped.getIOMode().respectIOConfig() && !wrapped.getConfig().getMode(side).canOutput())
                return false;
            return wrapped.canExtract();
        }

        @Override
        public boolean canReceive() {
            if (wrapped.getIOMode().respectIOConfig() && !wrapped.getConfig().getMode(side).canInput())
                return false;
            return wrapped.canReceive();
        }
    }
}
