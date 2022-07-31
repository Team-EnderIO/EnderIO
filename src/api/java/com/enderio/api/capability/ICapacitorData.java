package com.enderio.api.capability;

import com.enderio.api.capacitor.CapacitorModifier;
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
     * Get the level for the given capacitor modifier.
     */
    float getLevel(CapacitorModifier modifier);

    // Allows the interface to be serialized as a capability.
    @Override
    default String getSerializedName() {
        return "CapacitorData";
    }
}
