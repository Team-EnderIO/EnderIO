package com.enderio.conduits.common.capability;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;

public class ExtractionSpeedUpgrade implements ConduitUpgrade {
    private final int tier;

    public ExtractionSpeedUpgrade(int tier) {
        this.tier = tier;
    }

    public int tier() {
        return tier;
    }
}
