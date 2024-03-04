package com.enderio.api.attachment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Optional;

public class StoredEntityData implements INBTSerializable<CompoundTag> {
    private CompoundTag entityTag = new CompoundTag();
    private float maxHealth = 0.0f;

    /**
     * Should match key from {@link IEntityExtension#serializeNBT()}.
     */
    public static final String KEY_ID = "id";

    /**
     * Should match key from {@link Entity#saveWithoutId(CompoundTag)}
     */
    public static final String KEY_ENTITY = "Entity";
    private static final String KEY_HEALTH = "Health";
    private static final String KEY_MAX_HEALTH = "MaxHealth";

    public static StoredEntityData of(LivingEntity entity) {
        StoredEntityData data = new StoredEntityData();
        data.entityTag = entity.serializeNBT();
        data.maxHealth = entity.getMaxHealth();
        return data;
    }

    public static StoredEntityData of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_ID, entityType.toString());

        StoredEntityData data = new StoredEntityData();
        data.entityTag = tag;
        data.maxHealth = 0.0f;
        return data;
    }

    public static StoredEntityData empty() {
        StoredEntityData data = new StoredEntityData();
        data.maxHealth = 0.0f;
        return data;
    }

    public boolean hasEntity() {
        return getEntityType().isPresent();
    }

    public Optional<ResourceLocation> getEntityType() {
        CompoundTag tag = entityTag;
        if (tag.contains(KEY_ID)) {
            return Optional.of(new ResourceLocation(tag.getString(KEY_ID)));
        }

        return Optional.empty();
    }

    public CompoundTag getEntityTag() {
        return entityTag;
    }

    public Optional<Tuple<Float, Float>> getHealthState() {
        if (maxHealth > 0.0f) {
            CompoundTag tag = entityTag;
            if (tag.contains(KEY_HEALTH)) {
                return Optional.of(new Tuple<>(tag.getFloat(KEY_HEALTH), maxHealth));
            }
        }

        return Optional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.put(KEY_ENTITY, entityTag);
        if (maxHealth > 0.0f) {
            compound.putFloat(KEY_MAX_HEALTH, maxHealth);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        entityTag = tag.getCompound(KEY_ENTITY);
        if (tag.contains(KEY_MAX_HEALTH)) {
            maxHealth = tag.getFloat(KEY_MAX_HEALTH);
        }
    }
}
