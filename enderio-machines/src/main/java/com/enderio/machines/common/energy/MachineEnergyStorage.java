package com.enderio.machines.common.energy;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Energy storage intended for use with a machine that has a capacitor.
 * All base values are scaled with the capacitor base and specialisations.
 */
public class MachineEnergyStorage implements INBTSerializable<Tag>, IEnergyStorage {
    protected int storedEnergy;

    // TODO: Provide via constructor
    protected int baseCapacity = 10000;
    protected int baseMaxTransfer = 120;
    protected int baseMaxConsumption = 40;
    private final EnergyTransferMode transferMode;

    private final Supplier<Optional<ICapacitorData>> capacitorSupplier;

    public MachineEnergyStorage(Supplier<Optional<ICapacitorData>> capacitorSupplier, EnergyTransferMode transferMode) {
        this.capacitorSupplier = capacitorSupplier;
        this.transferMode = transferMode;
    }

    // region Getters

    @Override
    public int getEnergyStored() {
        // Stops the energy reading as over capacity.
        return Math.min(storedEnergy, getMaxEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
//        return Math.round(baseCapacity * capacitorSupplier.get().map(ICapacitorData::getBase).orElse(0.0f));
        return Math.round(capacitorSupplier.get().map(data -> data.getValue(MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get())).orElse(0.0f));
    }

    // TODO: Should the next two methods be exposed by an interface?

    /**
     * Maximum speed of energy consumption within the block itself.
     */
    public int getMaxEnergyConsumption() {
        return Math.round(baseMaxConsumption * capacitorSupplier.get().map(ICapacitorData::getBase).orElse(0.0f));
    }

    /**
     * Maximum speed of energy transfer in and out of the block.
     */
    public int getMaxEnergyTransfer() {
        return Math.round(baseMaxTransfer * capacitorSupplier.get().map(ICapacitorData::getBase).orElse(0.0f));
    }

    // endregion

    // region Energy Manipulation

    public void setStoredEnergy(int energy) {
        this.storedEnergy = Math.min(energy, getMaxEnergyStored());
        onEnergyChanged();
    }

    public void addEnergy(int energy) {
        this.storedEnergy = Math.min(this.storedEnergy + energy, getMaxEnergyStored());
        onEnergyChanged();
    }

    public boolean hasEnergy(int energy) {
        return storedEnergy >= energy;
    }

    /**
     * Consume energy.
     * Returns the amount of energy consumed.
     * @param energy
     * @return
     */
    public int consumeEnergy(int energy) {
        int energyConsumed = Math.min(energy, Math.min(getMaxEnergyConsumption(), energy));
        this.storedEnergy -= energyConsumed;
        return energyConsumed;
    }

    // endregion

    // region Forge Energy API Receive and Extract

    // TODO

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(getMaxEnergyTransfer(), maxReceive));
        if (!simulate) {
            addEnergy(energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxEnergyTransfer(), maxExtract));
        if (!simulate) {
            addEnergy(-energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public boolean canExtract() {
        return transferMode == EnergyTransferMode.Extract && getMaxEnergyTransfer() > 0;
    }

    @Override
    public boolean canReceive() {
        return transferMode == EnergyTransferMode.Insert && getMaxEnergyTransfer() > 0;
    }

    // endregion

    // Override in a BE to run setChanged
    protected void onEnergyChanged() { }

    // region Serialization

    @Override
    public Tag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(Tag nbt) {

    }

    // endregion
}
