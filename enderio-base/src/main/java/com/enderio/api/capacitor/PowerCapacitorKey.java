package com.enderio.api.capacitor;

/**
 * A capacitor key that scales on powers of the level.
 */
public final class PowerCapacitorKey extends CapacitorKey {
    public PowerCapacitorKey(float baseValue) {
        super(baseValue);
    }

    @Override
    public float getValue(float level) {
        return (float) Math.pow(base, level);
    }
}
