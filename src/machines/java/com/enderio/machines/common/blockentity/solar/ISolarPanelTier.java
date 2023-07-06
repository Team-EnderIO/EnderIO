package com.enderio.machines.common.blockentity.solar;

import com.enderio.machines.common.blockentity.multienergy.ICapacityTier;

public interface ISolarPanelTier extends ICapacityTier {
    int getProductionRate();
}
