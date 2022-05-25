package com.enderio.api.capacitor;

/**
 * A capacitor key that is linearly scaled based upon the level.
 */
public final class LinearCapacitorKey extends CapacitorKey {
    public LinearCapacitorKey(float baseValue) {
        super(baseValue);
    }

    @Override
    public float getValue(float level) {
        return base * level;
    }
}
