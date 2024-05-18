package com.enderio.api.capacitor;

import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Supplier;

public record SteppedScalable(
    CapacitorModifier modifier,
    Supplier<Integer> base,
    Supplier<Integer> step
) implements ICapacitorScalable {

    @Override
    public Supplier<Float> scaleF(NonNullSupplier<ICapacitorData> data) {
        return () -> scale(base.get(), step.get(), data.get().getModifier(modifier));
    }

    @Override
    public Supplier<Integer> scaleI(NonNullSupplier<ICapacitorData> data) {
        return () -> Math.round(scale(base.get(), step.get(), data.get().getModifier(modifier)));
    }

    private static float scale(int base, int step, float level) {
        return base + step * level;
    }
}
