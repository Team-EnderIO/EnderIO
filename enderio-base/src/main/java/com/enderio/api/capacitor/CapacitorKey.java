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
public abstract class CapacitorKey extends ForgeRegistryEntry<CapacitorKey> {

    protected final float base;

    public CapacitorKey(float baseValue) {
        this.base = baseValue;
    }

    /**
     * Get the value of the key at the given level.
     */
    public abstract float getValue(float level);

    /**
     * Get the value of the key using the capacitor data.
     */
    public float getValue(ICapacitorData data) {
        return getValue(data.getLevel(this));
    }
}
