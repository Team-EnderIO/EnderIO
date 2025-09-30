package com.enderio.enderio.api.soul;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a stored soul, derived from a {@link LivingEntity}.
 * @param entityTag the entity's NBT tag.
 */
public record Soul(@Nullable EntityType<?> entityType, CompoundTag entityTag) {
    private static final Codec<Soul> NEW_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(Soul::entityType),
            CompoundTag.CODEC.fieldOf("entity_tag").forGetter(Soul::entityTag)
        ).apply(instance, Soul::new));

    private static final Codec<Soul> OLD_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entityTag").forGetter(Soul::entityTag)
        ).apply(instance, Soul::new));

    public static final Codec<Soul> CODEC = Codec.withAlternative(NEW_CODEC, OLD_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Soul> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.ENTITY_TYPE),
        Soul::entityType,
        ByteBufCodecs.COMPOUND_TAG,
        Soul::entityTag,
        Soul::new
    );

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
        Leashable.LEASH_TAG,
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
        this(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(tag.getString(Entity.ID_TAG))), tag);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Soul> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Soul decode(RegistryFriendlyByteBuf byteBuf) {
            boolean hasEntity = byteBuf.readBoolean();
            if (!hasEntity) {
                return EMPTY;
            }

            return STREAM_CODEC.decode(byteBuf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf o, Soul soul) {
            o.writeBoolean(soul.hasEntity());
            if (soul.hasEntity()) {
                STREAM_CODEC.encode(o, soul);
            }
        }
    };

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

        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
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

    public Tag save(HolderLookup.Provider lookupProvider) {
        if (!this.hasEntity()) {
            throw new IllegalStateException("Cannot encode empty StoredEntityData");
        } else {
            return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
        }
    }

    public Tag saveOptional(HolderLookup.Provider lookupProvider) {
        return this.hasEntity() ? save(lookupProvider) : new CompoundTag();
    }

    public static Optional<Soul> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag)
            .resultOrPartial(error -> LOGGER.error("Tried to load invalid StoredEntityData: '{}'", error));
    }

    public static Soul parseOptional(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        return tag.isEmpty() ? EMPTY : parse(lookupProvider, tag).orElse(EMPTY);
    }
}
