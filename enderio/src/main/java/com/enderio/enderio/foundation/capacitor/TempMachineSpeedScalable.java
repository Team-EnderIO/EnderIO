package com.enderio.enderio.foundation.capacitor;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.scaling.CapacitorScalable;

public record TempMachineSpeedScalable(CapacitorScalable<Integer> energyUsage) implements CapacitorScalable<Float> {

    @Override
    public Float base() {
        return 1f;
    }

    @Override
    public Float scale(CapacitorData data) {
        return energyUsage.base() / (float)energyUsage.scale(data);
    }
}
