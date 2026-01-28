package com.enderio.enderio.content.glass;

import net.minecraft.util.StringRepresentable;

public enum GlassLighting implements StringRepresentable {
    NONE("none"),
    BLOCKING("blocking"),
    EMITTING("emitting");

    private final String name;

    GlassLighting(String name) {
        this.name = name;
    }

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

    @Override
    public String getSerializedName() {
        return name;
    }
}
