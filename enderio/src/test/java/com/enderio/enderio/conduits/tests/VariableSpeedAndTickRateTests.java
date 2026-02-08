package com.enderio.enderio.conduits.tests;

import com.enderio.enderio.api.conduits.connection.path.VariableSpeedAndTickRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class VariableSpeedAndTickRateTests {
    @Test
    public void testAdjustedSpeed() {
        VariableSpeedAndTickRate speedAndTickRate = new VariableSpeedAndTickRate(20, 32);
        int adjustedSpeed = speedAndTickRate.getAdjustedSpeed(10);
        Assertions.assertEquals(16, adjustedSpeed);
    }

    @Test
    public void testSpeedMinAggregation() {
        var allSpeeds = List.of(
            new VariableSpeedAndTickRate(64, 10),
            new VariableSpeedAndTickRate(32, 20)
        );

        var minProperty = VariableSpeedAndTickRate.minProperty();

        var outcome = minProperty.aggregate(allSpeeds);

        Assertions.assertEquals(32, outcome.speed());
        Assertions.assertEquals(20, outcome.tickRate());
    }
}
