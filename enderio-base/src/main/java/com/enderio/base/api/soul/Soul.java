package com.enderio.base.api.soul;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a stored soul, derived from a {@link LivingEntity}.
 * @param entityTag the entity's NBT tag.
 */
public record Soul(CompoundTag entityTag) {
    /**
     * Should match key from {@link IEntityExtension#serializeNBT(HolderLookup.Provider)}.
     */
    public static final String KEY_ID = "id";

    public static final Codec<Soul> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entityTag").forGetter(Soul::entityTag)
        ).apply(instance, Soul::new));

    public static final StreamCodec<ByteBuf, Soul> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG,
        Soul::getEntityTag,
        Soul::new
    );

    public static final StreamCodec<ByteBuf, Soul> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Soul decode(ByteBuf byteBuf) {
            boolean hasEntity = byteBuf.readBoolean();
            if (!hasEntity) {
                return EMPTY;
            }

            return STREAM_CODEC.decode(byteBuf);
        }

        @Override
        public void encode(ByteBuf o, Soul soul) {
            o.writeBoolean(soul.hasEntity());
            if (soul.hasEntity()) {
                STREAM_CODEC.encode(o, soul);
            }
        }
    };

    public static final Soul EMPTY = new Soul(new CompoundTag());

    public static Soul of(LivingEntity entity) {
        return new Soul(entity.serializeNBT(entity.level().registryAccess()));
    }

    public static Soul of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_ID, entityType.toString());
        return new Soul(tag);
    }

    public static Soul of(EntityType<?> entityType) {
        return of(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
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
        return Objects.equals(soul1.getEntityTag(), soul2.getEntityTag());
    }

    public static boolean isSameEntitySameTag(Soul soul, LivingEntity livingEntity, HolderLookup.Provider registries) {
        if (!isSameEntity(soul, livingEntity)) {
            return false;
        }

        var entityTag = livingEntity.serializeNBT(registries);
        return Objects.equals(soul.getEntityTag(), entityTag);
    }

    public boolean hasEntity() {
        if (!entityTag.contains(KEY_ID)) {
            return false;
        }

        var id = ResourceLocation.tryParse(entityTag.getString(KEY_ID));
        if (id == null) {
            return false;
        }

        var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
        return entityType.isPresent();
    }

    public boolean isEmpty() {
        return !hasEntity();
    }

    /**
     * @throws IllegalStateException if the soul is empty.
     * @return
     */
    public ResourceLocation entityTypeId() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot get Entity Type ID from empty StoredEntityData");
        }

        return ResourceLocation.parse(entityTag.getString(KEY_ID));
    }

    /**
     * @throws IllegalStateException if the soul is empty.
     * @return
     */
    public EntityType<?> entityType() {
        return BuiltInRegistries.ENTITY_TYPE.get(entityTypeId());
    }

    public CompoundTag getEntityTag() {
        return entityTag;
    }

    public Soul copy() {
        if (isEmpty()) {
            return EMPTY;
        }

        return new Soul(entityTag.copy());
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
