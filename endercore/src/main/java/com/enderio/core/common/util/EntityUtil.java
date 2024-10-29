package com.enderio.core.common.util;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

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
    public static String getEntityDescriptionId(ResourceLocation entityType) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        if (type == null) {
            return "error"; // TODO: Proper key
        }

        return type.getDescriptionId();
    }

    /**
     * Lookup an entity's type in the entity registry and get its resource location.
     *
     * @param entity The entity to lookup.
     * @return The resource location of the entity type.
     */
    public static Optional<ResourceLocation> getEntityTypeRL(Entity entity) {
        return Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }
}
