package com.enderio.base.api.capacitor;

import java.util.function.Supplier;

public record SteppedScalable(
    CapacitorModifier modifier,
    Supplier<Integer> base,
    Supplier<Integer> step
) implements CapacitorScalable {

    @Override
    public Supplier<Float> scaleF(Supplier<CapacitorData> data) {
        return () -> scale(base.get(), step.get(), data.get().getModifier(modifier));
    }

    @Override
    public Supplier<Integer> scaleI(Supplier<CapacitorData> data) {
        return () -> Math.round(scale(base.get(), step.get(), data.get().getModifier(modifier)));
    }

    private static float scale(int base, int step, float level) {
        return base + step * level;
    }
}
