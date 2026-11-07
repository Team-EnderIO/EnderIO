package com.enderio.enderio.content.armory.energy;

import com.enderio.enderio.api.armory.DarkSteelEnergyStorage;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorScalable;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class CapacitorBackedDarkSteelEnergyStorage extends CapacitorBackedEnergyStorage implements DarkSteelEnergyStorage {
    public CapacitorBackedDarkSteelEnergyStorage(ItemStack parent, DataComponentType<Integer> energyComponent, CapacitorScalable capacityScalable) {
        super(parent, energyComponent, capacityScalable);
    }

    @Override
    public boolean consumeEnergy(int toConsume, boolean simulate) {
        if (!canConsume()) {
            return false;
        }

        int energy = getEnergyStored();
        if (energy < toConsume) {
            return false;
        }

        if (!simulate) {
            setEnergy(energy - toConsume);
        }

        return true;
    }

    @Override
    public boolean canConsume() {
        // If we have a capacitor, we can consume energy.
        return !getCapacitorData().equals(CapacitorData.NONE);
    }
}
