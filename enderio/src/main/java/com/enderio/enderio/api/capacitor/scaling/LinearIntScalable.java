package com.enderio.enderio.api.capacitor.scaling;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;

import java.util.function.Supplier;

/**
 * A value that is scaled linearly.
 * (base, level) => base * level
 */
public record LinearIntScalable(CapacitorModifier modifier, Supplier<Integer> baseGetter) implements CapacitorScalable<Integer> {
    @Override
    public Integer base() {
        return baseGetter.get();
    }

    @Override
    public Integer scale(CapacitorData data) {
        return Math.round(baseGetter.get() * data.getModifier(modifier));
    }
}
