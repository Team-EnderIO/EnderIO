package com.enderio.core.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * Helper utilities for dealing with entities.
 */
public class EntityUtil {

    /**
     * Get the description ID from an entity type in the registry.
     *
     * @param entityType The entity type to get a description ID for.
     * @return The description ID.
     */
    public static String getEntityDescriptionId(Identifier entityType) {
        // TODO: Proper key for error state
        return BuiltInRegistries.ENTITY_TYPE
            .getOptional(entityType)
            .map(EntityType::getDescriptionId).orElse("error");
    }

    /**
     * Lookup an entity's type in the entity registry and get its resource location.
     *
     * @param entity The entity to lookup.
     * @return The resource location of the entity type.
     */
    public static Optional<Identifier> getEntityTypeRL(Entity entity) {
        return Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }
}
