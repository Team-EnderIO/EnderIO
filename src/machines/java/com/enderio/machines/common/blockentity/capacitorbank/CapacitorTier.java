package com.enderio.machines.common.blockentity.capacitorbank;

import com.enderio.machines.common.blockentity.multienergy.CapacityTier;
import com.enderio.machines.common.config.MachinesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum CapacitorTier implements CapacityTier {

    BASIC(MachinesConfig.COMMON.ENERGY.BASIC_CAPACITOR_BANK_CAPACITY),
    ADVANCED(MachinesConfig.COMMON.ENERGY.ADVANCED_CAPACITOR_BANK_CAPACITY),
    VIBRANT(MachinesConfig.COMMON.ENERGY.VIBRANT_CAPACITOR_BANK_CAPACITY);

    private final ModConfigSpec.ConfigValue<Integer> capacity;

    CapacitorTier(ModConfigSpec.ConfigValue<Integer> capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getStorageCapacity() {
        return capacity.get();
    }
}

