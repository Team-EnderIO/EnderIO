package com.enderio.api.capacitor;

import java.util.Map;

// TODO: End game capacitor fabrication with mob fighting? Souls? Capacitor sacrifice?

// TODO: Capacitor filter for conduits.

/**
 * Capacitor data.
 * This defines a capacitor that stores.
 * Can be attached to an item as a capability.
 */
public interface ICapacitorData {
    /**
     * Get the base modifier of the capacitor.
     */
    float getBase();

    /**
     * Get the modifier value for the given capacitor modifier type.
     */
    float getModifier(CapacitorModifier modifier);

    /**
     * Get a map of all modifiers and levels.
     * @implNote When implementing this, only return modifiers that have been changed, don't return 1's as these will pollute tooltips.
     */
    Map<CapacitorModifier, Float> getAllModifiers();
}
