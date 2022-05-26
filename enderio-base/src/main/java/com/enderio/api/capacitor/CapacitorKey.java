package com.enderio.api.capacitor;

import com.enderio.api.capability.ICapacitorData;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * A capacitor key.
 * This is a named key which declares a base value and scales based on a "level".
 * This level is provided by a capacitor's data.
 * <p>
 * Should implement {@link #getValue(float)} to define how scaling works.
 */
public final class CapacitorKey extends ForgeRegistryEntry<CapacitorKey> {

    private final float base;

    private final IScaler scaler;

    public CapacitorKey(float baseValue, IScaler scaler) {
        this.base = baseValue;
        this.scaler = scaler;
    }

    /**
     * Get the base value of the key without scaling.
     */
    public float getBase() {
        return base;
    }

    /**
     * Get the value of the key at the given level.
     */
    public float getValue(float level) {
        return scaler.scale(base, level);
    }

    /**
     * Get the value of the key using the capacitor data.
     */
    public float getValue(ICapacitorData data) {
        return getValue(data.getLevel(this));
    }

    public int getInt(float level) {
        return Math.round(getValue(level));
    }

    public int getInt(ICapacitorData data) {
        return getInt(data.getLevel(this));
    }
}
