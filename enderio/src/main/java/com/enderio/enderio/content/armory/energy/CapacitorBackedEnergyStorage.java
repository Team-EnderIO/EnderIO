package com.enderio.enderio.content.armory.energy;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.armory.DarkSteelEnergyStorage;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorScalable;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CapacitorBackedEnergyStorage implements IEnergyStorage {
    protected final ItemStack parent;
    protected final DataComponentType<Integer> energyComponent;
    protected final CapacitorScalable capacityScalable;

    public CapacitorBackedEnergyStorage(ItemStack parent, DataComponentType<Integer> energyComponent, CapacitorScalable capacityScalable) {
        this.parent = parent;
        this.energyComponent = energyComponent;
        this.capacityScalable = capacityScalable;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energy = getEnergyStored();
        int energyReceived = Mth.clamp(getMaxEnergyStored() - energy, 0, toReceive);
        if (!simulate && energyReceived > 0) {
            setEnergy(energy + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        int rawEnergy = parent.getOrDefault(energyComponent, 0);
        return Mth.clamp(rawEnergy, 0, getMaxEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return capacityScalable.scaleI(this::getCapacitorData).get();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        // If we have a capacitor, we can receive energy.
        return !getCapacitorData().equals(CapacitorData.NONE);
    }

    protected void setEnergy(int energy) {
        parent.set(energyComponent, energy);
    }

    protected CapacitorData getCapacitorData() {
        var capacitorSocket = parent.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (capacitorSocket == null) {
            return CapacitorData.NONE;
        }

        return capacitorSocket.getCapacitorData();
    }
}
