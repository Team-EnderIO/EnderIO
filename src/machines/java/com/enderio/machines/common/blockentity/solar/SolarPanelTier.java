package com.enderio.machines.common.blockentity.solar;

import com.enderio.machines.common.config.MachinesConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public enum SolarPanelTier implements ISolarPanelTier {

    SIMPLE(MachinesConfig.COMMON.ENERGY.SIMPLE_SOLAR_PANEL_MAX_PRODUCTION),
    BASIC(MachinesConfig.COMMON.ENERGY.BASIC_SOLAR_PANEL_MAX_PRODUCTION),
    ADVANCED(MachinesConfig.COMMON.ENERGY.ADVANCED_SOLAR_PANEL_MAX_PRODUCTION),
    VIBRANT(MachinesConfig.COMMON.ENERGY.VIBRANT_SOLAR_PANEL_MAX_PRODUCTION);

    private final ForgeConfigSpec.ConfigValue<Integer> productionRate;

    SolarPanelTier(ForgeConfigSpec.ConfigValue<Integer> productionRate) {
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

