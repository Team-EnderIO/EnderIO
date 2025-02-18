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
    protected void setEnergy(int energy) {
        super.setEnergy(energy);
        // Do this to ensure any equipped items have their ItemAttributeModifierEvent
        // event fired so they can update
        // any attribute modifiers when energy is lost or regained
        parent.set(ArmoryDataComponents.DARK_STEEL_ITEM_HAS_ENERGY, energy > 0);
    }
}
