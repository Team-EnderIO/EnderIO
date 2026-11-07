package com.enderio.enderio.content.armory.energy;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorScalable;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class DarkSteelEnergyStorage implements IEnergyStorage {
    private final MutableDataComponentHolder parent;
    private final DataComponentType<ItemStack> capacitorComponent;
    private final DataComponentType<Integer> energyComponent;
    private final CapacitorScalable capacityScalable;

    public DarkSteelEnergyStorage(MutableDataComponentHolder parent, DataComponentType<ItemStack> capacitorComponent,
        DataComponentType<Integer> energyComponent, CapacitorScalable capacityScalable) {
        this.parent = parent;
        this.capacitorComponent = capacitorComponent;
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

    private void setEnergy(int energy) {
        parent.set(energyComponent, energy);
    }

    private CapacitorData getCapacitorData() {
        var capacitorStack = parent.get(capacitorComponent);
        if (capacitorStack == null) {
            return CapacitorData.NONE;
        }

        var capacitorExtension = capacitorStack.getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION);
        if (capacitorExtension != null) {
            var data = capacitorExtension.getCapacitorData(capacitorStack, null);
            if (data != null) {
                return data;
            }
        }

        return capacitorStack.getOrDefault(EIODataComponents.CAPACITOR_DATA, CapacitorData.NONE);
    }
}
