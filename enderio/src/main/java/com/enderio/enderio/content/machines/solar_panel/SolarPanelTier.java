package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.enderio.config.machines.MachinesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum SolarPanelTier implements ISolarPanelTier {

    ENERGETIC(MachinesConfig.COMMON.ENERGY.ENERGETIC_SOLAR_PANEL_MAX_PRODUCTION),
    PULSATING(MachinesConfig.COMMON.ENERGY.PULSATING_SOLAR_PANEL_MAX_PRODUCTION),
    VIBRANT(MachinesConfig.COMMON.ENERGY.VIBRANT_SOLAR_PANEL_MAX_PRODUCTION);

    private final ModConfigSpec.IntValue productionRate;

    SolarPanelTier(ModConfigSpec.IntValue productionRate) {
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

