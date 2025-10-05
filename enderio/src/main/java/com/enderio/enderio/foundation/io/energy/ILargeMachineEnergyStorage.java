package com.enderio.enderio.foundation.io.energy;

/**
 * Machine energy storage extensions.
 */
// NOTE: Using 'I' prefix here for consistency with Neo EnergyStorage
public interface ILargeMachineEnergyStorage {

    long getLargeEnergyStored();

    long getLargeMaxEnergyStored();
}
