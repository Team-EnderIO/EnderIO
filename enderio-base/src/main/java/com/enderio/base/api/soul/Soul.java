package com.enderio.base.api.soul;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public record Soul(CompoundTag entityTag, float maxHealth) {
    /**
     * Should match key from {@link IEntityExtension#serializeNBT(HolderLookup.Provider)}.
     */
    public static final String KEY_ID = "id";

    /**
     * Should match key from {@link LivingEntity#addAdditionalSaveData(CompoundTag)}
     */
    public static final String KEY_HEALTH = "Health";

    public static Codec<Soul> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entityTag").forGetter(Soul::entityTag),
            Codec.FLOAT.fieldOf("maxHealth").forGetter(Soul::maxHealth)
        ).apply(instance, Soul::new));

    public static StreamCodec<ByteBuf, Soul> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG,
        Soul::getEntityTag,
        ByteBufCodecs.FLOAT,
        Soul::maxHealth,
        Soul::new
    );

    public static StreamCodec<ByteBuf, Soul> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
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

    public static final Soul EMPTY = new Soul(
        new CompoundTag(),
        0.0f
    );

    public static Soul of(LivingEntity entity) {
        return new Soul(
            entity.serializeNBT(entity.level().registryAccess()),
            entity.getMaxHealth()
        );
    }

    public static Soul of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_ID, entityType.toString());

        return new Soul(tag, 0.0f);
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
        return entityType() != null;
    }

    public boolean isEmpty() {
        return entityType() == null;
    }

    // TODO: Might make these non-null and throw if used without checking hasEntity/isEmpty?
    @Nullable
    public EntityType<?> entityType() {
        var id = entityTypeId();
        if (id != null) {
            return BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        }

        return null;
    }

    @Nullable
    public ResourceLocation entityTypeId() {
        if (entityTag.contains(KEY_ID)) {
            return ResourceLocation.tryParse(entityTag.getString(KEY_ID));
        }

        return null;
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

    public Soul copy() {
        return new Soul(entityTag.copy(), maxHealth);
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
