package com.enderio.base.api.travel;

import net.minecraft.resources.ResourceLocation;

public interface TravelTargetType<T extends TravelTarget> {
    static <T extends TravelTarget> TravelTargetType<T> simple(final ResourceLocation name) {
        final String toString = name.toString();
        return new TravelTargetType<>() {
            @Override
            public String toString() {
                return toString;
            }
        };
    }
}
