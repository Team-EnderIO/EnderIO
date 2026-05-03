package com.enderio.enderio.tests.conduits;

import com.enderio.enderio.api.conduits.connection.path.SpeedAndTickRatePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SpeedAndTickRatePairTests {
    @Test
    public void testAdjustedSpeed() {
        SpeedAndTickRatePair speedAndTickRate = new SpeedAndTickRatePair(32, 20);
        int adjustedSpeed = speedAndTickRate.getAdjustedSpeed(10);
        Assertions.assertEquals(16, adjustedSpeed);

        // Should clamp to lowest near value
        adjustedSpeed = speedAndTickRate.getAdjustedSpeed(11);
        Assertions.assertEquals(17, adjustedSpeed);

        adjustedSpeed = speedAndTickRate.getAdjustedSpeed(5);
        Assertions.assertEquals(8, adjustedSpeed);

        adjustedSpeed = speedAndTickRate.getAdjustedSpeed(0);
        Assertions.assertEquals(0, adjustedSpeed);

        Assertions.assertThrows(IllegalArgumentException.class, () -> speedAndTickRate.getAdjustedSpeed(-10));
        Assertions.assertThrows(IllegalArgumentException.class, () -> speedAndTickRate.getAdjustedSpeed(21));
        Assertions.assertThrows(IllegalArgumentException.class, () -> speedAndTickRate.getAdjustedSpeed(100));
    }

    @Test
    public void testAdjustedSpeedForZero() {
        SpeedAndTickRatePair speedAndTickRate = SpeedAndTickRatePair.ZERO;

        Assertions.assertEquals(0, speedAndTickRate.getAdjustedSpeed(20));
        Assertions.assertEquals(0, speedAndTickRate.getAdjustedSpeed(10));
        Assertions.assertEquals(0, speedAndTickRate.getAdjustedSpeed(5));
        Assertions.assertEquals(0, speedAndTickRate.getAdjustedSpeed(1));
        Assertions.assertEquals(0, speedAndTickRate.getAdjustedSpeed(0));
    }

    @Test
    public void testSpeedMinAggregation() {
        var allSpeeds = List.of(
            new SpeedAndTickRatePair(64, 10),
            new SpeedAndTickRatePair(32, 20)
        );

        var minProperty = SpeedAndTickRatePair.minProperty(SpeedAndTickRatePair.ZERO);

        var outcome = minProperty.aggregate(allSpeeds);

        Assertions.assertEquals(32, outcome.speed());
        Assertions.assertEquals(20, outcome.tickRate());
    }
}
