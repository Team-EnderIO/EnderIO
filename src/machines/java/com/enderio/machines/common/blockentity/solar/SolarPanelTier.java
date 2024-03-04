package com.enderio.machines.common.blockentity.solar;

import com.enderio.machines.common.config.MachinesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum SolarPanelTier implements ISolarPanelTier {

    ENERGETIC(MachinesConfig.COMMON.ENERGY.ENERGETIC_SOLAR_PANEL_MAX_PRODUCTION),
    PULSATING(MachinesConfig.COMMON.ENERGY.PULSATING_SOLAR_PANEL_MAX_PRODUCTION),
    VIBRANT(MachinesConfig.COMMON.ENERGY.VIBRANT_SOLAR_PANEL_MAX_PRODUCTION);

    private final ModConfigSpec.ConfigValue<Integer> productionRate;

    SolarPanelTier(ModConfigSpec.ConfigValue<Integer> productionRate) {
        this.productionRate = productionRate;
    }

    @Override
    public int getProductionRate() {
        return productionRate.get();
    }

    @Override
    public int getStorageCapacity() {
        return getProductionRate() * 1000;
    }
}

