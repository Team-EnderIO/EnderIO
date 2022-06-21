package com.enderio.machines.common.io.energy;

import com.enderio.api.energy.EnergyIOMode;
import com.enderio.api.io.IIOConfig;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Machine energy storage extensions.
 */
public interface IMachineEnergyStorage extends IEnergyStorage {
    /**
     * Set the amount of energy inside the storage
     */
    void setEnergyStored(int energy);

    /**
     * Add energy into storage.
     * @return Amount of energy added.
     */
    int addEnergy(int energy);

    /**
     * Remove energy from storage.
     * @apiNote Uncapped, if you are consuming energy, use {@link #consumeEnergy(int)} instead.
     * @return Amount of energy taken.
     */
    int takeEnergy(int energy);

    /**
     * Consume energy from storage.
     * @apiNote This is capped to {@link #getMaxEnergyUse()}, for uncapped use use {@link #takeEnergy(int)} instead.
     * @return Amount of energy consumed.
     */
    int consumeEnergy(int energy);

    /**
     * Get the max energy transfer rate.
     * Generally used for limiting external IO.
     */
    int getMaxEnergyTransfer();

    /**
     * Get the maximum energy use rate for the storage.
     * Generally this is the consumption speed or generation speed.
     */
    int getMaxEnergyUse();

    /**
     * Get the IO config for the storage.
     * This is generally provided by the machine.
     */
    IIOConfig getConfig();

    /**
     * Get the energy IO mode for the storage
     * Determines how the IO config modifies behaviour.
     */
    EnergyIOMode getIOMode();
}
