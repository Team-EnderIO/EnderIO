package com.enderio.enderio.api.capacitor.scaling;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;

import java.util.function.Supplier;

/**
 * A value that is scaled using a quadratic equation.
 * (baseG, level) => base * level^2
 */
public record QuadraticIntScalable(CapacitorModifier modifier, Supplier<Integer> baseGetter) implements CapacitorScalable<Integer> {
    @Override
    public Integer base() {
        return baseGetter.get();
    }

    @Override
    public Integer scale(CapacitorData data) {
        float level = data.getModifier(modifier);
        return Math.round(baseGetter.get() * level * level);
    }
}
