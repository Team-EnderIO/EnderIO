package com.enderio.core.common.components;

// TODO: Not a fan of this pattern, remove it.
public interface ItemEnergyStorageConfig {
    int getMaxEnergy();

    default int getMaxExtract() {
        return getMaxEnergy();
    }

    default int getMaxReceive() {
        return getMaxEnergy();
    }
}
