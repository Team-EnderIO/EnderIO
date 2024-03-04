package com.enderio.api.capacitor;

import java.util.function.Supplier;

/**
 * A value that is scaled using a quadratic equation.
 * (base, level) => base * level^2
 */
public record QuadraticScalable(CapacitorModifier modifier, Supplier<Integer> base) implements ICapacitorScalable {

    @Override
    public Supplier<Float> scaleF(Supplier<ICapacitorData> data) {
        return () -> scale(base.get(), data.get().getModifier(modifier));
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<ICapacitorData> data) {
        return () -> Math.round(scale(base.get(), data.get().getModifier(modifier)));
    }

    private static float scale(int base, float level) {
        return base * level * level;
    }
}
