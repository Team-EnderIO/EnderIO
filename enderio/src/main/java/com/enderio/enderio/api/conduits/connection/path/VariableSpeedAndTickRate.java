package com.enderio.enderio.api.conduits.connection.path;

import java.util.Comparator;

public record VariableSpeedAndTickRate(int speed, int tickRate) {
    public static final VariableSpeedAndTickRate ZERO = new VariableSpeedAndTickRate(0, 0);

    public static ConnectionPathProperty<VariableSpeedAndTickRate> minProperty() {
        return new ConnectionPathProperty<>(allSpeeds -> allSpeeds.stream().min(VariableSpeedAndTickRate::compare).orElse(ZERO));
    }

    public int getAdjustedSpeed(int otherTickRate) {
        final int maxSpeedPerTick = speed() * (20 / tickRate());
        return maxSpeedPerTick / (20 / otherTickRate);
    }

    public static int compare(VariableSpeedAndTickRate o1, VariableSpeedAndTickRate o2) {
        return Integer.compare(o1.getAdjustedSpeed(20), o2.getAdjustedSpeed(20));
    }
}
