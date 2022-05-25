package com.enderio.api.capacitor;

public class IndexedScaler implements IScaler {
    private final float scale;
    private final float[] keyValues;

    public IndexedScaler(float scale, float... keyValues) {
        this.scale = scale;
        this.keyValues = keyValues;
    }

    @Override
    public float scale(float value, float level) {
        // Get or interpolate the multiplier
        float scaledIdx = level / scale;
        int idxI = (int) scaledIdx;
        float idxF = scaledIdx - idxI;

        float scale;
        if (idxI < 0) {
            scale = keyValues[0];
        } else if (idxI >= keyValues.length) {
            scale = keyValues[keyValues.length - 1];
        } else {
            scale = (1 - idxF) * keyValues[idxI] + idxF * keyValues[idxI + 1];
        }

        // Scale :)
        return value * scale;
    }
}
