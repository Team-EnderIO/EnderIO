package com.enderio.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public class StoredEntityData implements INBTSerializable<Tag> {
    private CompoundTag entityTag = new CompoundTag();
    private float maxHealth = 0.0f;

    /**
     * Should match key from {@link IForgeEntity#serializeNBT()}.
     */
    public static final String KEY_ID = "id";

    /**
     * Should match key from {@link Entity#saveWithoutId(CompoundTag)}
     */
    public static final String KEY_ENTITY = "Entity";
    private static final String KEY_HEALTH = "Health";
    private static final String KEY_MAX_HEALTH = "MaxHealth";

    public StoredEntityData() {
    }

    public StoredEntityData(CompoundTag entityTag, float maxHealth) {
        this.entityTag = entityTag;
        this.maxHealth = maxHealth;
    }

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
    public Tag serializeNBT() {
        var compound = new CompoundTag();
        compound.put(KEY_ENTITY, entityTag);
        if (maxHealth > 0.0f) {
            compound.putFloat(KEY_MAX_HEALTH, maxHealth);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            entityTag = compoundTag.getCompound(KEY_ENTITY);
            if (compoundTag.contains(KEY_MAX_HEALTH)) {
                maxHealth = compoundTag.getFloat(KEY_MAX_HEALTH);
            }
        }
    }
}
