package com.enderio.machines.common.blocks.base.energy;

import com.enderio.base.api.io.IOConfigurable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record EnergyStorageInfo(int energyStored, int maxEnergyStored) implements IMachineEnergyStorage {

    public static EnergyStorageInfo of(IEnergyStorage storage) {
        return new EnergyStorageInfo(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public EnergyStorageInfo withEnergyStored(int energyStored) {
        return new EnergyStorageInfo(energyStored, maxEnergyStored);
    }

    // TODO: IMachineEnergyStorage is temporary to support the existing energy
    // widgets.

    @Override
    public void setEnergyStored(int energy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addEnergy(int energy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addEnergy(int energy, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int takeEnergy(int energy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxEnergyUse() {
        // No machines display this yet?
        return 0;
    }

    @Override
    public IOConfigurable getConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnergyIOMode getIOMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        throw new UnsupportedOperationException();
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
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
