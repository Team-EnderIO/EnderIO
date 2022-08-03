package com.enderio.api.capacitor;

import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Supplier;

/**
 * A scalable value is a value which can be scaled based on a capacitor.
 */
public interface ICapacitorScalable {
    Supplier<Float> scaleF(NonNullSupplier<ICapacitorData> data);
    Supplier<Integer> scaleI(NonNullSupplier<ICapacitorData> data);
}
