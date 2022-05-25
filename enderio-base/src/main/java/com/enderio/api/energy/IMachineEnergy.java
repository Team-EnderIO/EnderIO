package com.enderio.api.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IMachineEnergy {
    /**
     * Get total energy stored in the machine.
     */
    int getEnergyStored();

    /**
     * Add energy to the machine.
     */
    void addEnergy(int energy);

    /**
     * Consume machine energy.
     * @apiNote Normally for use internally only.
     *
     * @todo Should this be moved to PoweredMachineEntity and made protected?
     */
    int consumeEnergy(int energy);

    /**
     * Get energy capacity.
     */
    int getMaxEnergyStored();

    /**
     * Get max energy consumption rate.
     *
     * @todo Actually respect consumption limits.
     */
    int getMaxEnergyConsumption();

    /**
     * Get max energy transfer rate.
     */
    int getMaxEnergyTransfer();

    /**
     * Get energy leakage rate.
     *
     * @todo Implement leaking.
     */
    int getEnergyLeakRate();

    /**
     * Whether energy can be inserted via the given side.
     * @apiNote Should check the side of access when it matters, usually only for power buffers/storage.
     */
    boolean canInsertEnergy(@Nullable Direction side);

    /**
     * Whether energy can be extracted from the given side.
     * @apiNote Should check the side of access when it matters, usually only for power buffers/storage.
     */
    boolean canExtractEnergy(@Nullable Direction side);

    /**
     * Get the energy and max energy as a pair value.
     */
    default EnergyCapacityPair getEnergyCapacityPair() {
        return new EnergyCapacityPair(getEnergyStored(), getMaxEnergyStored());
    }
}
