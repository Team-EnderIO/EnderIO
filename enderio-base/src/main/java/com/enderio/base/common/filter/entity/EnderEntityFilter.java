package com.enderio.base.common.filter.entity;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.filter.EntityFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

// TODO: should tag comparison compare health?
public record EnderEntityFilter(NonNullList<StoredEntityData> matches, boolean isDenyList, boolean shouldCompareTags)
    implements EntityFilter {

    public static final EnderEntityFilter EMPTY = new EnderEntityFilter(0);

    // TODO: 1.22 Rename fields.
    public static Codec<EnderEntityFilter> CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, StoredEntityData.CODEC, StoredEntityData.EMPTY)
                    .fieldOf("entities")
                    .forGetter(EnderEntityFilter::matches),
                Codec.BOOL.fieldOf("isInvert").forGetter(EnderEntityFilter::isDenyList),
                Codec.BOOL.fieldOf("nbt").forGetter(EnderEntityFilter::shouldCompareTags))
            .apply(componentInstance, EnderEntityFilter::new));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderEntityFilter> STREAM_CODEC = StreamCodec.composite(
        StoredEntityData.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderEntityFilter::matches,
        ByteBufCodecs.BOOL,
        EnderEntityFilter::isDenyList,
        ByteBufCodecs.BOOL,
        EnderEntityFilter::shouldCompareTags,
        EnderEntityFilter::new);
    // @formatter:on

    public EnderEntityFilter(int size) {
        this(NonNullList.withSize(size, StoredEntityData.EMPTY), false, false);
    }

    public EnderEntityFilter(List<StoredEntityData> matches, boolean isDenyList, boolean shouldCompareComponents) {
        this(NonNullList.withSize(matches.size(), StoredEntityData.EMPTY), isDenyList, shouldCompareComponents);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }

    @Override
    public boolean test(LivingEntity entity) {
        for (var match : matches) {
            if (match.entityType().isPresent()) {
                // Check for type match
                if (!match.entityType().get().equals(EntityType.getKey(entity.getType()))) {
                    return isDenyList;
                }

                // Check components
                if (shouldCompareTags) {
                    CompoundTag tag = entity.serializeNBT(entity.level().registryAccess());
                    if (tag.equals(match.getEntityTag())) {
                        return !isDenyList;
                    }
                } else {
                    return !isDenyList;
                }
            }
        }

        return isDenyList;
    }

    @Override
    public boolean test(StoredEntityData storedEntity) {
        // Empty never passes.
        if (storedEntity.entityType().isEmpty()) {
            return false;
        }

        for (var match : matches) {
            if (match.entityType().isPresent()) {
                // Check for type match
                if (!match.entityType().get().equals(storedEntity.entityType().get())) {
                    return isDenyList;
                }

                // Check components
                if (shouldCompareTags) {
                    if (storedEntity.getEntityTag().equals(match.getEntityTag())) {
                        return !isDenyList;
                    }
                } else {
                    return !isDenyList;
                }
            }
        }

        return isDenyList;
    }

    @Override
    public boolean test(EntityType<?> entity) {
        for (var match : matches) {
            if (match.entityType().isPresent()) {
                return !isDenyList && match.entityType().get().equals(EntityType.getKey(entity));
            }
        }

        return isDenyList;
    }
}
