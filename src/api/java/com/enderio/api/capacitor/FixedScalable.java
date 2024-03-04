package com.enderio.api.capacitor;

import java.util.function.Supplier;

/**
 * A fixed value that implements ICapacitorScalable.
 * This is a utility and will always return the value provided in the constructor.
 */
public record FixedScalable(Supplier<Integer> value) implements ICapacitorScalable {
    public static final FixedScalable ZERO = new FixedScalable(() -> 0);

    @Override
    public Supplier<Float> scaleF(Supplier<ICapacitorData> data) {
        return () -> (float) value.get();
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<ICapacitorData> data) {
        return value;
    }
}
