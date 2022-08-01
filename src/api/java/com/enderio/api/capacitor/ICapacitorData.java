package com.enderio.api.capacitor;

import com.enderio.api.nbt.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

/**
 * Capacitor data.
 * This defines a capacitor that stores.
 * Can be attached to an item as a capability.
 */
public interface ICapacitorData extends INamedNBTSerializable<Tag> {
    /**
     * Get the base modifier of the capacitor.
     */
    float getBase();

    /**
     * Get the modifier value for the given capacitor modifier type.
     */
    float getModifier(CapacitorModifier modifier);

    // Allows the interface to be serialized as a capability.
    @Override
    default String getSerializedName() {
        return "CapacitorData";
    }
}
