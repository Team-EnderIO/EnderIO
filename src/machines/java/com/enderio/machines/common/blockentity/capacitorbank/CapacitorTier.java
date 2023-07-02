package com.enderio.machines.common.blockentity.capacitorbank;

import com.enderio.machines.common.blockentity.multienergy.ICapacityTier;
import com.enderio.machines.common.config.MachinesConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public enum CapacitorTier implements ICapacityTier {

    BASIC(MachinesConfig.COMMON.BASIC_CAPACITOR_BANK_CAPACITY),
    ADVANCED(MachinesConfig.COMMON.ADVANCED_CAPACITOR_BANK_CAPACITY),
    VIBRANT(MachinesConfig.COMMON.VIBRANT_CAPACITOR_BANK_CAPACITY);

    private final ForgeConfigSpec.ConfigValue<Integer> capacity;

    CapacitorTier(ForgeConfigSpec.ConfigValue<Integer> capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getStorageCapacity() {
        return capacity.get();
    }
}

