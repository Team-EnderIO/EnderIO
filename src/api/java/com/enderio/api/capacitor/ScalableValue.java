package com.enderio.api.capacitor;

import com.enderio.api.capability.ICapacitorData;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Supplier;

// A scalable value contains the key type (to be renamed), the base value and the scaler of the value.
// This is static across all machinery.
// It uses a supplier for the base value, so we can use configs to populate it.
public record ScalableValue(CapacitorModifier modifier, Supplier<Float> base, IScaler scaler) {
    public static ScalableValue of(CapacitorModifier modifier, float base, IScaler scaler) {
        return new ScalableValue(modifier, () -> base, scaler);
    }

    public static ScalableValue fixed(float base) {
        return new ScalableValue(CapacitorModifier.FIXED, () -> base, Scalers.FIXED);
    }

    public static ScalableValue fixed(Supplier<Float> base) {
        return new ScalableValue(CapacitorModifier.FIXED, base, Scalers.FIXED);
    }

    public Supplier<Float> scale(NonNullSupplier<ICapacitorData> data) {
        return () -> scaler.scale(base.get(), data.get().getLevel(modifier));
    }

    public Supplier<Integer> scaleRounded(NonNullSupplier<ICapacitorData> data) {
        return () -> Math.round(scaler.scale(base.get(), data.get().getLevel(modifier)));
    }
}
