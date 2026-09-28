package com.enderio.enderio.api.capacitor.scaling;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;

import java.util.function.Supplier;

public record SteppedIntScalable(
    CapacitorModifier modifier,
    Supplier<Integer> baseGetter,
    Supplier<Integer> stepGetter
) implements CapacitorScalable<Integer> {
    @Override
    public Integer base() {
        return baseGetter.get();
    }

    @Override
    public Integer scale(CapacitorData data) {
        return Math.round(baseGetter.get() + stepGetter.get() * data.getModifier(modifier));
    }
}
