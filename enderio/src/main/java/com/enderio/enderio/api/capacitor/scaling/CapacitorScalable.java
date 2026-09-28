package com.enderio.enderio.api.capacitor.scaling;

import com.enderio.enderio.api.capacitor.CapacitorData;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * A scalable value is a value which can be scaled based on a capacitor.
 * This is designed so that things scaled by capacitors can be provided into non-specific domains.
 * It also allows for the base value of a scalable to be powered by configs.
 */
public interface CapacitorScalable<T extends Number> {
    T base();
    T scale(CapacitorData data);

    @ApiStatus.NonExtendable
    default Supplier<T> scaled(Supplier<CapacitorData> data) {
        return () -> scale(data.get());
    }
}
