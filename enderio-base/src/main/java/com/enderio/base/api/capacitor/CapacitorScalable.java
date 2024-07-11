package com.enderio.base.api.capacitor;

import java.util.function.Supplier;

/**
 * A scalable value is a value which can be scaled based on a capacitor.
 * This is designed so that things scaled by capacitors can be provided into non-specific domains.
 * It also allows for the base value of a scalable to be powered by configs.
 */
public interface CapacitorScalable {
    Supplier<Float> scaleF(Supplier<CapacitorData> data);
    Supplier<Integer> scaleI(Supplier<CapacitorData> data);
}
