package com.enderio.api.capacitor;

public enum Scalers implements IScaler {
    // TODO: Serious naming overhaul xD
    LINEAR((v, l) -> v * l),
    POW((v, l) -> (float)Math.pow(v, l)),
    LINEAR_2_MINUS_1((v, l) -> v * ((2 * l) - 1))
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
