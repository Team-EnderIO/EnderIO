package com.enderio.api.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Optional;

public record StoredEntityData(CompoundTag entityTag, float maxHealth) {
    /**
     * Should match key from {@link IEntityExtension#serializeNBT(HolderLookup.Provider)}.
     */
    public static final String KEY_ID = "id";

    /**
     * Should match key from {@link LivingEntity#addAdditionalSaveData(CompoundTag)}
     */
    public static final String KEY_HEALTH = "Health";

    public static Codec<StoredEntityData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entityTag").forGetter(StoredEntityData::entityTag),
            Codec.FLOAT.fieldOf("maxHealth").forGetter(StoredEntityData::maxHealth)
        ).apply(instance, StoredEntityData::new));

    public static StreamCodec<ByteBuf, StoredEntityData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG,
        StoredEntityData::getEntityTag,
        ByteBufCodecs.FLOAT,
        StoredEntityData::maxHealth,
        StoredEntityData::new
    );

    public static final StoredEntityData EMPTY = new StoredEntityData(
        new CompoundTag(),
        0.0f
    );

    public static StoredEntityData of(LivingEntity entity) {
        return new StoredEntityData(
            entity.serializeNBT(entity.level().registryAccess()),
            entity.getMaxHealth()
        );
    }

    public static StoredEntityData of(ResourceLocation entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_ID, entityType.toString());

        return new StoredEntityData(tag, 0.0f);
    }

    // TODO: Make null = none (in the component layer)?
    public boolean hasEntity() {
        return entityType().isPresent();
    }

    public Optional<ResourceLocation> entityType() {
        if (entityTag.contains(KEY_ID)) {
            return Optional.of(new ResourceLocation(entityTag.getString(KEY_ID)));
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
}
