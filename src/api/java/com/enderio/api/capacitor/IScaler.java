package com.enderio.api.capacitor;

/**
 * A scaling function.
 */
public interface IScaler {
    /**
     * Scale value by the given level.
     */
    float scale(float value, float level);
}
