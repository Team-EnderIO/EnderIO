package com.enderio.core.common.attachment;

// TODO: Move to component package
public interface IItemEnergyConfig {
    int getMaxEnergy();

    default int getMaxExtract() {
        return getMaxEnergy();
    }

    default int getMaxReceive() {
        return getMaxEnergy();
    }
}
