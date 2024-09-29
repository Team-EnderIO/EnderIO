package com.enderio.base.api.capacitor;

import java.util.function.Supplier;

/**
 * A fixed value that implements ICapacitorScalable.
 * This is a utility and will always return the value provided in the constructor.
 */
public record FixedScalable(Supplier<Integer> value) implements CapacitorScalable {
    public static final FixedScalable ZERO = new FixedScalable(() -> 0);

    @Override
    public Supplier<Float> scaleF(Supplier<CapacitorData> data) {
        return () -> (float) value.get();
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<CapacitorData> data) {
        return value;
    }
}
