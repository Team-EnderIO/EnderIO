package com.enderio.api.capacitor;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;

/**
 * Capacitor key types, for use in loot capacitors for targeting increases to general stats.
 */
public enum CapacitorModifier {
    ENERGY_CAPACITY,
    ENERGY_USE,

    /**
     * This should always go last as the loot picker will exclude the final item in this enum
     * @apiNote Capacitors should never multiply the FIXED modifiers...
     */
    FIXED;

    public final ResourceLocation id;

    public static final List<CapacitorModifier> SELECTABLE_MODIFIERS = List.of(
        ENERGY_CAPACITY,
        ENERGY_USE
    );

    CapacitorModifier() {
        this.id = new ResourceLocation("enderio", "capacitor." + name().toLowerCase(Locale.ROOT));
    }
}
