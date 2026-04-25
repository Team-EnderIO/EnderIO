package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.multienergy.CapacityTier;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum CapacitorTier implements CapacityTier {

    BASIC(MachinesConfig.COMMON.ENERGY.BASIC_CAPACITOR_BANK_CAPACITY),
    ADVANCED(MachinesConfig.COMMON.ENERGY.ADVANCED_CAPACITOR_BANK_CAPACITY),
    VIBRANT(MachinesConfig.COMMON.ENERGY.VIBRANT_CAPACITOR_BANK_CAPACITY);

    private final ModConfigSpec.IntValue capacity;

    CapacitorTier(ModConfigSpec.IntValue capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getStorageCapacity() {
        return capacity.get();
    }
}

