package com.enderio.base.api.attachment;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.slf4j.Logger;

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

    public boolean hasEntity() {
        return entityType().isPresent();
    }

    public Optional<ResourceLocation> entityType() {
        if (entityTag.contains(KEY_ID)) {
            return Optional.of(ResourceLocation.parse(entityTag.getString(KEY_ID)));
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
