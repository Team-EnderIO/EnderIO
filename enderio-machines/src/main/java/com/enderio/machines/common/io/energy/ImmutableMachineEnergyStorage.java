package com.enderio.machines.common.io.energy;

import com.enderio.api.energy.EnergyIOMode;
import com.enderio.api.io.IIOConfig;
import org.apache.commons.lang3.NotImplementedException;

/**
 * An immutable machine energy storage.
 * Used for client side syncing.
 */
public class ImmutableMachineEnergyStorage implements IMachineEnergyStorage {
    /**
     * A default value, storing no energy.
     */
    public static final ImmutableMachineEnergyStorage EMPTY = new ImmutableMachineEnergyStorage(0, 0, 0, 0);

    private final int energyStored;
    private final int maxEnergyStored;
    private final int maxEnergyTransfer;
    private final int maxEnergyUse;

    public ImmutableMachineEnergyStorage(int energyStored, int maxEnergyStored, int maxEnergyTransfer, int maxEnergyUse) {
        this.energyStored = energyStored;
        this.maxEnergyStored = maxEnergyStored;
        this.maxEnergyTransfer = maxEnergyTransfer;
        this.maxEnergyUse = maxEnergyUse;
    }

    public ImmutableMachineEnergyStorage(IMachineEnergyStorage storage) {
        this(storage.getEnergyStored(), storage.getMaxEnergyStored(), storage.getMaxEnergyTransfer(), storage.getMaxEnergyUse());
    }

    @Override
    public int getEnergyStored() {
        return energyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public int getMaxEnergyTransfer() {
        return maxEnergyTransfer;
    }

    @Override
    public int getMaxEnergyUse() {
        return maxEnergyUse;
    }

    @Override
    public IIOConfig getConfig() {
        // TODO: Should this be implemented?
        throw new NotImplementedException();
    }

    @Override
    public EnergyIOMode getIOMode() {
        // TODO: Should this be implemented?
        throw new NotImplementedException();
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public void setEnergyStored(int energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public int addEnergy(int energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public int takeEnergy(int energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This storage is immutable.
     */
    @Deprecated
    @Override
    public int consumeEnergy(int energy) {
        throw new UnsupportedOperationException();
    }
}
