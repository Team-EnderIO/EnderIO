package com.enderio.api.capacitor;

/**
 * Capacitor key types, for use in loot capacitors for targeting increases to general stats.
 */
public enum CapacitorModifier {
    ENERGY_CAPACITY, ENERGY_USE, ENERGY_TRANSFER,

    /**
     * @apiNote Capacitors should never multiply the FIXED modifiers...
     */
    FIXED
}
