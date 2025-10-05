package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.enderio.foundation.block.entity.multienergy.CapacityTier;

// TODO: I want to drop the I from all our interfaces. This one has a name conflict.
public interface ISolarPanelTier extends CapacityTier {
    int getProductionRate();
}
