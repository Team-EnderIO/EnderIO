package com.enderio.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public class StoredEntityData implements INBTSerializable<Tag> {
    private CompoundTag entityTag = new CompoundTag();
    private float maxHealth = 0.0f;

    public static StoredEntityData of(LivingEntity entity) {
        StoredEntityData data = new StoredEntityData();
        data.entityTag = entity.serializeNBT();
        data.maxHealth = entity.getMaxHealth();
        return data;
    }

    public static StoredEntityData of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", entityType.toString());

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
        if (tag.contains("id")) {
            return Optional.of(new ResourceLocation(tag.getString("id")));
        }

        return Optional.empty();
    }

    public CompoundTag getEntityTag() {
        return entityTag;
    }

    public Optional<Tuple<Float, Float>> getHealthState() {
        if (maxHealth > 0.0f) {
            CompoundTag tag = entityTag;
            if (tag.contains("Health")) {
                return Optional.of(new Tuple<>(tag.getFloat("Health"), maxHealth));
            }
        }

        return Optional.empty();
    }

    @Override
    public Tag serializeNBT() {
        var compound = new CompoundTag();
        compound.put("Entity", entityTag);
        if (maxHealth > 0.0f) {
            compound.putFloat("MaxHealth", maxHealth);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            entityTag = compoundTag.getCompound("Entity");
            if (compoundTag.contains("MaxHealth")) {
                maxHealth = compoundTag.getFloat("MaxHealth");
            }
        }
    }
}
