package com.enderio.base.common.capability.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public class StoredEntityData implements INBTSerializable<Tag> {
    private Optional<CompoundTag> entityTag = Optional.empty();
    private Optional<Float> maxHealth = Optional.empty();

    public static StoredEntityData of(LivingEntity entity) {
        StoredEntityData data = new StoredEntityData();
        data.entityTag = Optional.of(entity.serializeNBT());
        data.maxHealth = Optional.of(entity.getMaxHealth());
        return data;
    }

    public static StoredEntityData of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", entityType.toString());

        StoredEntityData data = new StoredEntityData();
        data.entityTag = Optional.of(tag);
        data.maxHealth = Optional.empty();
        return data;
    }

    public static StoredEntityData empty() {
        StoredEntityData data = new StoredEntityData();
        data.entityTag = Optional.empty();
        data.maxHealth = Optional.empty();
        return data;
    }

    public Optional<ResourceLocation> getEntityType() {
        if (entityTag.isPresent()) {
            CompoundTag tag = entityTag.get();
            if (tag.contains("id")) {
                return Optional.of(new ResourceLocation(tag.getString("id")));
            }
        }

        return Optional.empty();
    }

    public Optional<CompoundTag> getEntityTag() {
        return entityTag;
    }

    public Optional<Tuple<Float, Float>> getHealthState() {
        if (entityTag.isPresent() && maxHealth.isPresent()) {
            CompoundTag tag = entityTag.get();
            float maxH = maxHealth.get();
            if (tag.contains("Health")) {
                return Optional.of(new Tuple<>(tag.getFloat("Health"), maxH));
            }
        }

        return Optional.empty();
    }

    @Override
    public Tag serializeNBT() {
        var compound = new CompoundTag();
        entityTag.ifPresent(tag -> compound.put("Entity", tag));
        maxHealth.ifPresent(maxH -> compound.putFloat("MaxHealth", maxH));
        return compound;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            if (compoundTag.contains("Entity")) {
                entityTag = Optional.of(compoundTag.getCompound("Entity"));
            }

            if (compoundTag.contains("MaxHealth")) {
                maxHealth = Optional.of(compoundTag.getFloat("MaxHealth"));
            }
        }
    }
}
