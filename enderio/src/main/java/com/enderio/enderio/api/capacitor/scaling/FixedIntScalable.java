package com.enderio.enderio.api.capacitor.scaling;

import com.enderio.enderio.api.capacitor.CapacitorData;

import java.util.function.Supplier;

/**
 * A fixed value that implements ICapacitorScalable.
 * This is a utility and will always return the value provided in the constructor.
 */
public record FixedIntScalable(Supplier<Integer> value) implements CapacitorScalable<Integer> {
    public static final FixedIntScalable ZERO = new FixedIntScalable(() -> 0);

    @Override
    public Integer base() {
        return value.get();
    }

    @Override
    public Integer scale(CapacitorData data) {
        return value.get();
    }
}
