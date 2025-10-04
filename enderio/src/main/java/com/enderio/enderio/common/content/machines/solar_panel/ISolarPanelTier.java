package com.enderio.enderio.common.content.machines.solar_panel;

import com.enderio.enderio.common.foundation.block.entity.multienergy.CapacityTier;

// TODO: I want to drop the I from all our interfaces. This one has a name conflict.
public interface ISolarPanelTier extends CapacityTier {
    int getProductionRate();
}
