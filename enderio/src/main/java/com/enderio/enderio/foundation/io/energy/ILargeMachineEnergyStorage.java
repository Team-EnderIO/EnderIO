package com.enderio.enderio.foundation.io.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Machine energy storage extensions.
 */
// NOTE: Using 'I' prefix here for consistency with Neo EnergyStorage
public interface ILargeMachineEnergyStorage extends IEnergyStorage {

    long getLargeEnergyStored();

    long getLargeMaxEnergyStored();
}
