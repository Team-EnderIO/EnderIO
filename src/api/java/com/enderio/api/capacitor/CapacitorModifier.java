package com.enderio.api.capacitor;

import net.minecraft.resources.ResourceLocation;

/**
 * Capacitor key types, for use in loot capacitors for targeting increases to general stats.
 */
public enum CapacitorModifier {
    ENERGY_CAPACITY, ENERGY_USE,

    /**
     * @apiNote Capacitors should never multiply the FIXED modifiers...
     */
    FIXED;

    public final ResourceLocation id;

    CapacitorModifier() {
        this.id = new ResourceLocation("enderio", "capacitor." + name().toLowerCase());
    }
}
