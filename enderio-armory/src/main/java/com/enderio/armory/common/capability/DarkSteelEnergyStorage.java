package com.enderio.armory.common.capability;

import com.enderio.armory.common.init.ArmoryDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

public class DarkSteelEnergyStorage extends ComponentEnergyStorage {

    public DarkSteelEnergyStorage(MutableDataComponentHolder parent, DataComponentType<Integer> energyComponent,
            int capacity, int maxTransfer) {
        super(parent, energyComponent, capacity, maxTransfer);
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return super.receiveEnergy(toReceive, simulate);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return super.extractEnergy(toExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return super.canExtract();
    }

    @Override
    public boolean canReceive() {
        return super.canReceive();
    }

    @Override
    protected void setEnergy(int energy) {
        super.setEnergy(energy);
        // Do this to ensure any equipped items have their ItemAttributeModifierEvent
        // event fired so they can update
        // any attribute modifiers energy is lost or regained
        parent.set(ArmoryDataComponents.DARK_STEEL_ITEM_HAS_ENERGY, energy > 0);
    }
}
