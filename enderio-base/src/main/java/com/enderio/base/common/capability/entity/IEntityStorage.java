package com.enderio.base.common.capability.entity;

import com.enderio.base.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;

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
    @Nonnull
    StoredEntityData getStoredEntityData();

    /**
     * Set the entity NBT.
     */
    void setStoredEntityData(StoredEntityData entity);
}
