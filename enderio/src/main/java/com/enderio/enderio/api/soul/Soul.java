package com.enderio.enderio.api.soul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a stored soul, derived from a {@link LivingEntity}.
 * @param entityTag the entity's NBT tag.
 */
public record Soul(@Nullable EntityType<?> entityType, CompoundTag entityTag) { //TODO can ValueOutput be used instead of the tag? Couldn't find a codex for it
    public static final Soul EMPTY = new Soul(null, new CompoundTag());

    public static final Codec<Soul> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(Soul::entityType),
            CompoundTag.CODEC.fieldOf("entity_tag").forGetter(Soul::entityTag)
        ).apply(instance, Soul::new));

    public static final Codec<Soul> OPTIONAL_CODEC = ExtraCodecs
        .optionalEmptyMap(CODEC).xmap((optionalSoul) -> optionalSoul.orElse(EMPTY), (soul) -> soul.isEmpty() ? Optional.empty() : Optional.of(soul));

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
        Entity.TAG_ID, // We store the entity type separately to the entity data.
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
        Entity.TAG_UUID
    );

    // Do not compare obviously unreasonable NBT Keys
    private static final List<String> IGNORED_KEYS_DURING_COMPARISON = List.of(
        // TODO: need to check for any other tags that should be ignored.
        // Perhaps make this configurable?
        Bee.TAG_CANNOT_ENTER_HIVE_TICKS,
        Bee.TAG_TICKS_SINCE_POLLINATION,
        Bee.TAG_CROPS_GROWN_SINCE_POLLINATION,
        Bee.TAG_HIVE_POS,
        Entity.TAG_PASSENGERS
    );

    public Soul {
        // Remove tags we don't want
        IGNORED_KEYS.forEach(entityTag::remove);
    }

    // TODO: Can this be trusted? I feel like it needs better validation...
    public Soul(CompoundTag entityTag) {
        this(BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.parse(entityTag.getStringOr(Entity.TAG_ID, "pig"))), entityTag); //TODO better default
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

    public static Soul of(LivingEntity entity) {
        var entityTag = TagValueOutput.createWithContext(new ProblemReporter.Collector(), entity.level().registryAccess());
        entity.saveWithoutId(entityTag);
        return new Soul(entity.getType(), entityTag.buildResult());
    }

    public static Soul of(ResourceLocation entityType) {
        return of(BuiltInRegistries.ENTITY_TYPE.getValue(entityType));
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

        var entityTagToCompare = TagValueOutput.createWithoutContext(new ProblemReporter.Collector());
        livingEntity.saveWithoutId(entityTagToCompare);
        return isSameTag(soul.entityTag(), entityTagToCompare.buildResult());
    }

    private static boolean isSameTag(CompoundTag tag1, CompoundTag tag2) {
        var allKeys = Stream.concat(tag1.keySet().stream(), tag2.keySet().stream()).collect(Collectors.toSet());
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
        tag.putString(Entity.TAG_ID, entityTypeId().toString());
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

    public ValueInput asInput(HolderLookup.Provider lookup) {
        return TagValueInput.create(new ProblemReporter.Collector(), lookup, entityTag);
    }
}
