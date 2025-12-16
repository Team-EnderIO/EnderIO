package com.enderio.enderio.api.poi;

import net.minecraft.resources.ResourceLocation;

public interface EnderPOIType<T extends EnderPOI> {
    static <T extends EnderPOI> EnderPOIType<T> simple(final ResourceLocation name) {
        final String toString = name.toString();
        return new EnderPOIType<>() {
            @Override
            public String toString() {
                return toString;
            }
        };
    }
}
