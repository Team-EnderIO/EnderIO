package com.enderio.enderio.foundation.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public record EnergyStorageInfo(long energy, long capacity) {

    public static EnergyStorageInfo of(EnergyHandler storage) {
        return new EnergyStorageInfo(storage.getAmountAsLong(), storage.getCapacityAsLong());
    }

    public EnergyStorageInfo withEnergy(long energy) {
        return new EnergyStorageInfo(energy, capacity);
    }
}
