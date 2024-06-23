package com.enderio.base.common.block.glass;

public enum GlassLighting {
    NONE,
    BLOCKING,
    EMITTING;

    public String shortName() {
        return switch (this) {
            case NONE -> "";
            case BLOCKING -> "d";
            case EMITTING -> "e";
        };
    }

    public String englishName() {
        return switch (this) {
            case NONE -> "";
            case BLOCKING -> "Dark";
            case EMITTING -> "Enlightened";
        };
    }
}
