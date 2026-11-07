package com.enderio.enderio.api.armory;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface DarkSteelEnergyStorage extends IEnergyStorage {
    // Consuming energy is different to extracting energy
    boolean consumeEnergy(int toConsume, boolean simulate);
    boolean canConsume();
}
