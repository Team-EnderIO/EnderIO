package com.enderio.enderio.api.capacitor;

import java.util.function.Supplier;

/**
 * A value that tends towards zero as the capacitor level increases.
 * (base, level) => base / level
 */
public record InverseScalable(CapacitorModifier modifier, Supplier<Integer> base) implements CapacitorScalable {

    @Override
    public Supplier<Float> scaleF(Supplier<CapacitorData> data) {
        return () -> scale(base.get(), data.get().getModifier(modifier));
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<CapacitorData> data) {
        return () -> Math.round(scale(base.get(), data.get().getModifier(modifier)));
    }

    private static float scale(int base, float level) {
        // Levels below 1 (e.g. no capacitor) cost the full base amount and avoid dividing by zero.
        return base / Math.max(level, 1.0f);
    }
}
