package com.enderio.enderio.tests.capacitor;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.LinearIntScalable;
import com.enderio.enderio.foundation.capacitor.TempMachineSpeedScalable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TempMachineSpeedScalableTests {
    @Test
    public void testNoScaling() {
        var energyScale = new LinearIntScalable(CapacitorModifier.ENERGY_USE, () -> 100);
        var speedScale = new TempMachineSpeedScalable(energyScale);

        CapacitorData data = new CapacitorData(1, Map.of());

        Assertions.assertEquals(1, speedScale.scale(data));
    }
    @Test
    public void testScalesLinearly() {
        var energyScale = new LinearIntScalable(CapacitorModifier.ENERGY_USE, () -> 100);
        var speedScale = new TempMachineSpeedScalable(energyScale);

        CapacitorData data = new CapacitorData(1, Map.of(CapacitorModifier.ENERGY_USE, 2f));

        // Expect a 0.5 speed modifier as a result of the energy use doubling.
        Assertions.assertEquals(0.5f, speedScale.scale(data));
    }
}
