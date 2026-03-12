package com.enderio.enderio.api.soul;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a stored soul, derived from a {@link LivingEntity}.
 * @param entityTag the entity's NBT tag.
 */
public record Soul(@Nullable EntityType<?> entityType, CompoundTag entityTag) {

    public static final String KEY_ENTITY_TAG = "Entity";

    // Keys that should not be compared or saved
    // Note be careful adding new things to this list - it will affect saves.
    private static final List<String> IGNORED_KEYS = List.of(
        Entity.ID_TAG, // We store the entity type separately to the entity data.
        "Air",
        "Brain",
        "DeathTime",
        "FallDistance",
        "FallFlying",
        "HurtByTimestamp",
        "HurtTime",
        "Motion",
        "OnGround",
        "PortalCooldown",
        "Pos",
        "Rotation",
        "SleepingX",
        "SleepingY",
        "SleepingZ",
        Mob.LEASH_TAG,
        Entity.UUID_TAG
    );

    // Do not compare obviously unreasonable NBT Keys
    private static final List<String> IGNORED_KEYS_DURING_COMPARISON = List.of(
        // TODO: need to check for any other tags that should be ignored.
        // Perhaps make this configurable?
        Bee.TAG_CANNOT_ENTER_HIVE_TICKS,
        Bee.TAG_TICKS_SINCE_POLLINATION,
        Bee.TAG_CROPS_GROWN_SINCE_POLLINATION,
        Bee.TAG_HIVE_POS,
        Entity.PASSENGERS_TAG
    );

    public Soul {
        // Remove tags we don't want
        IGNORED_KEYS.forEach(entityTag::remove);
    }

    // Legacy data support.
    private Soul(CompoundTag tag) {
        // Note: doesn't remove ID from the tag to ensure backwards compatibility.
        // TODO: 1.22 - remove this.
        this(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(tag.getString(Entity.ID_TAG))), tag);
    }

    public static final Soul EMPTY = new Soul(null, new CompoundTag());

    public static Soul of(LivingEntity entity) {
        var entityTag = new CompoundTag();
        entity.saveWithoutId(entityTag);
        return new Soul(entity.getType(), entityTag);
    }

    public static Soul of(ResourceLocation entityType) {
        return of(BuiltInRegistries.ENTITY_TYPE.get(entityType));
    }

    public static Soul of(EntityType<?> entityType) {
        return new Soul(entityType, new CompoundTag());
    }

    public static boolean isSameEntity(Soul soul1, Soul soul2) {
        return Objects.equals(soul1.entityType(), soul2.entityType());
    }

    public static boolean isSameEntity(Soul soul, LivingEntity livingEntity) {
        return isSameEntity(soul, livingEntity.getType());
    }

    public static boolean isSameEntity(Soul soul, EntityType<?> entityType) {
        return Objects.equals(soul.entityType(), entityType);
    }

    public static boolean isSameEntitySameTag(Soul soul1, Soul soul2) {
        if (!isSameEntity(soul1, soul2)) {
            return false;
        }

        return isSameTag(soul1.entityTag(), soul2.entityTag());
    }

    public static boolean isSameEntitySameTag(Soul soul, LivingEntity livingEntity) {
        if (!isSameEntity(soul, livingEntity)) {
            return false;
        }

        var entityTagToCompare = new CompoundTag();
        livingEntity.saveWithoutId(entityTagToCompare);
        return isSameTag(soul.entityTag(), entityTagToCompare);
    }

    private static boolean isSameTag(CompoundTag tag1, CompoundTag tag2) {
        var allKeys = Stream.concat(tag1.getAllKeys().stream(), tag2.getAllKeys().stream()).collect(Collectors.toSet());
        for (var key : allKeys) {
            if (IGNORED_KEYS.contains(key) ||
                IGNORED_KEYS_DURING_COMPARISON.contains(key)) {
                continue;
            }

            if (!Objects.equals(tag1.get(key), tag2.get(key))) {
                return false;
            }
        }

        return true;
    }

    public boolean hasEntity() {
        return entityType != null;
    }

    public boolean isEmpty() {
        return entityType == null;
    }

    /**
     * @throws IllegalStateException if the soul is empty.
     * @return
     */
    public ResourceLocation entityTypeId() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot get Entity Type ID from empty StoredEntityData");
        }

        return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
    }

    public CompoundTag getEntityTagWithId() {
        var tag = entityTag.copy();
        tag.putString(Entity.ID_TAG, entityTypeId().toString());
        return tag;
    }

    public Soul copy() {
        if (isEmpty()) {
            return EMPTY;
        }

        return new Soul(entityType(), entityTag.copy());
    }

    public Soul copyOnlyType() {
        if (isEmpty()) {
            return EMPTY;
        }

        return of(entityType());
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    public CompoundTag writeToNbt(CompoundTag tag) {
        if (isEmpty()) {
            return tag;
        }

        tag.putString(Entity.ID_TAG, entityTypeId().toString());
        tag.put(KEY_ENTITY_TAG, entityTag);

        return tag;
    }

    public static Soul loadFromNbt(CompoundTag tag) {
        if (!tag.contains(Entity.ID_TAG, Tag.TAG_STRING)) {
            // Support legacy 1.20.1 save format.
            if (tag.contains(KEY_ENTITY_TAG, Tag.TAG_COMPOUND)) {
                return new Soul(tag.getCompound(KEY_ENTITY_TAG));
            }
            return EMPTY;
        }

        ResourceLocation entityTypeId = new ResourceLocation(tag.getString(Entity.ID_TAG));
        CompoundTag entityTag = tag.getCompound(KEY_ENTITY_TAG);
        return new Soul(ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId), entityTag);
    }
}
