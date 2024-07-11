package com.enderio.base.api.capacitor;

import java.util.function.Supplier;

/**
 * A value that is scaled using a quadratic equation.
 * (base, level) => base * level^2
 */
public record QuadraticScalable(CapacitorModifier modifier, Supplier<Integer> base) implements CapacitorScalable {

    @Override
    public Supplier<Float> scaleF(Supplier<CapacitorData> data) {
        return () -> scale(base.get(), data.get().getModifier(modifier));
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<CapacitorData> data) {
        return () -> Math.round(scale(base.get(), data.get().getModifier(modifier)));
    }

    private static float scale(int base, float level) {
        return base * level * level;
    }
}
