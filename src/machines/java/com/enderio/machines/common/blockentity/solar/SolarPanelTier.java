package com.enderio.machines.common.blockentity.solar;

public enum SolarPanelTier implements ISolarPanelTier {

    SIMPLE(10),
    BASIC(40),
    ADVANCED(80),
    VIBRANT(160);

    private final int productionRate;
    private final int storageCapacity;

    SolarPanelTier(int productionRate) {
        this.productionRate = productionRate;
        this.storageCapacity = productionRate * 1000;
    }

    @Override
    public int getProductionRate() {
        return productionRate;
    }

    @Override
    public int getStorageCapacity() {
        return storageCapacity;
    }
}

