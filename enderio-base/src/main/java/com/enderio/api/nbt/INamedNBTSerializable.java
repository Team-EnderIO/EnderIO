package com.enderio.api.nbt;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Makes an NBT serializable object have it's own name.
 */
public interface INamedNBTSerializable<T extends Tag> extends INBTSerializable<T> {
    /**
     * Get the serialized name.
     * Must not change based on the state!
     */
    String getSerializedName();
}
