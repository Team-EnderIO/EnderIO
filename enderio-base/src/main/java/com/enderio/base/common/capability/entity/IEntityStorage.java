package com.enderio.base.common.capability.entity;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Optional;

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
