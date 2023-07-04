package com.enderio.api.capability;

/**
 * A capability used for storing an entity inside of an item/block.
 */
public interface IEntityStorage {

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
