package com.enderio.api.capacitor;

public enum Scalers implements IScaler {
    /**
     * Fixed is here to support "simple" capacitor keys. Makes it easy to add them into the system than to do special edge case work.
     */
    FIXED((v, l) -> v),
    LINEAR((v, l) -> v * l),
    POW((v, l) -> (float)Math.pow(v, l)),
    QUADRATIC((v, l) -> (float)(v * Math.pow(l, 2))),
    LINEAR_2_MINUS_1((v, l) -> v * ((2 * l) - 1)),
    ENERGY(new IndexedScaler(1f, 0, 1, 3, 5, 8, 13, 18))
    ;

    private final IScaler scaler;

    Scalers(IScaler scaler) {
        this.scaler = scaler;
    }

    @Override
    public float scale(float value, float level) {
        return scaler.scale(value, level);
    }
}
