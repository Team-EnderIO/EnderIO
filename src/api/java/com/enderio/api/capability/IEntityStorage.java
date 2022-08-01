package com.enderio.api.capability;

import com.enderio.api.nbt.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

/**
 * A capability used for storing an entity inside of an item/block.
 */
public interface IEntityStorage extends INamedNBTSerializable<Tag> {
    @Override
    default String getSerializedName() {
        return "EntityStorage";
    }

    default boolean hasStoredEntity() {
        return getStoredEntityData().getEntityType().isPresent();
    }

    /**
     * Get the entity NBT tag.
     * Generally used for creating the entity.
     */
    StoredEntityData getStoredEntityData();

    /**
     * Set the entity NBT.
     */
    void setStoredEntityData(StoredEntityData entity);
}
