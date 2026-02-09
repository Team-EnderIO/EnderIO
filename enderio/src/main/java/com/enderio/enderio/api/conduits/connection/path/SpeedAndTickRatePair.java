package com.enderio.enderio.api.conduits.connection.path;

import com.google.common.base.Preconditions;

/**
 * Represents a speed and tick rate pair.
 * @param speed the speed (every tickRate)
 * @param tickRate the tick rate
 */
public record SpeedAndTickRatePair(int speed, int tickRate) {
    public static final SpeedAndTickRatePair ZERO = new SpeedAndTickRatePair(0, 0);

    /**
     * @return a connection path property that minimises the adjusted speed.
     */
    public static ConnectionPathProperty<SpeedAndTickRatePair> minProperty(SpeedAndTickRatePair defaultValue) {
        return new ConnectionPathProperty<>(allSpeeds -> allSpeeds.stream().min(SpeedAndTickRatePair::compare), defaultValue);
    }

    /**
     * @return a connection path property that maximises the adjusted speed.
     */
    public static ConnectionPathProperty<SpeedAndTickRatePair> maxProperty(SpeedAndTickRatePair defaultValue) {
        return new ConnectionPathProperty<>(allSpeeds -> allSpeeds.stream().max(SpeedAndTickRatePair::compare), defaultValue);
    }

    public SpeedAndTickRatePair {
        Preconditions.checkArgument(speed >= 0, "speed must be non-negative");
        Preconditions.checkArgument(tickRate >= 0, "tickRate must be positive");
        Preconditions.checkArgument(tickRate <= 20, "tickRate must be <= 20");
    }

    /**
     * Get the speed adjusted for the given tick rate.
     * @param otherTickRate the tick rate to adjust for
     * @return the adjusted speed
     */
    public int getAdjustedSpeed(int otherTickRate) {
        Preconditions.checkArgument(otherTickRate >= 0, "otherTickRate must be positive");
        Preconditions.checkArgument(otherTickRate <= 20, "otherTickRate must be <= 20");

        // Short cut if we have no speed.
        if (speed() == 0 || tickRate() == 0) {
            return 0;
        }

        final double maxSpeedPerTick = Math.round(speed() * (20.0 / tickRate()));
        final double adjustedSpeed = maxSpeedPerTick / (20.0 / otherTickRate);
        return (int)Math.floor(adjustedSpeed);
    }

    /**
     * Compares two speed and tick rate pairs based on adjusted speed to 20 ticks.
     * @param o1 the first pair to compare
     * @param o2 the second pair to compare
     * @return the result of the comparison
     */
    public static int compare(SpeedAndTickRatePair o1, SpeedAndTickRatePair o2) {
        return Integer.compare(o1.getAdjustedSpeed(20), o2.getAdjustedSpeed(20));
    }
}
