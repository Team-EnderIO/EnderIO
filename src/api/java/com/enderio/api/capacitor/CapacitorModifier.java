package com.enderio.api.capacitor;

import net.minecraft.resources.ResourceLocation;

/**
 * Capacitor key types, for use in loot capacitors for targeting increases to general stats.
 */
public enum CapacitorModifier {
    ENERGY_CAPACITY("capacitor.energy_capacity"),
    ENERGY_USE("capacitor.energy_use"),
    ENERGY_TRANSFER("capacitor.energy_transfer"),

    /**
     * @apiNote Capacitors should never multiply the FIXED modifiers...
     */
    FIXED("capacitor.fixed");

    public final ResourceLocation id;

    CapacitorModifier(String id) {
        this.id = new ResourceLocation("enderio", id);
    }
}
