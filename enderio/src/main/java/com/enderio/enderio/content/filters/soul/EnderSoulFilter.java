package com.enderio.enderio.content.filters.soul;

import com.enderio.core.common.serialization.OrderedListCodec;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.soul.Soul;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Objects;

// TODO: should tag comparison compare health?
public record EnderSoulFilter(NonNullList<Soul> matches, boolean isDenyList, boolean shouldCompareTags)
    implements SoulFilter, TooltipComponent {

    public static final EnderSoulFilter EMPTY = new EnderSoulFilter(0);

    // TODO: 1.22 Rename fields.
    public static final Codec<EnderSoulFilter> CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, Soul.OPTIONAL_CODEC, Soul.EMPTY)
                    .fieldOf("entities")
                    .forGetter(EnderSoulFilter::matches),
                Codec.BOOL.fieldOf("isInvert").forGetter(EnderSoulFilter::isDenyList),
                Codec.BOOL.fieldOf("nbt").forGetter(EnderSoulFilter::shouldCompareTags))
            .apply(componentInstance, EnderSoulFilter::new));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderSoulFilter> STREAM_CODEC = StreamCodec.composite(
        Soul.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderSoulFilter::matches,
        ByteBufCodecs.BOOL,
        EnderSoulFilter::isDenyList,
        ByteBufCodecs.BOOL,
        EnderSoulFilter::shouldCompareTags,
        EnderSoulFilter::new);
    // @formatter:on

    public EnderSoulFilter(int size) {
        this(NonNullList.withSize(size, Soul.EMPTY), false, false);
    }

    public EnderSoulFilter(List<Soul> matches, boolean isDenyList, boolean shouldCompareTags) {
        this(NonNullList.withSize(matches.size(), Soul.EMPTY), isDenyList, shouldCompareTags);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }

    @Override
    public boolean test(LivingEntity entity) {
        for (var match : matches) {
            if (match.hasEntity()) {
                if (shouldCompareTags ? Soul.isSameEntitySameTag(match, entity) : Soul.isSameEntity(match, entity)) {
                    return !isDenyList;
                }
            }
        }

        return isDenyList;
    }

    @Override
    public boolean test(Soul soul) {
        // Empty never passes.
        if (soul.isEmpty()) {
            return false;
        }

        for (var match : matches) {
            if (!match.isEmpty()) {
                if (shouldCompareTags ? Soul.isSameEntitySameTag(match, soul) : Soul.isSameEntity(match, soul)) {
                    return !isDenyList;
                }
            }
        }

        return isDenyList;
    }

    @Override
    public boolean test(EntityType<?> entityType) {
        for (var match : matches) {
            if (!match.isEmpty()) {
                if (Soul.isSameEntity(match, entityType)) {
                   return !isDenyList;
                }
            }
        }

        return isDenyList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnderSoulFilter that = (EnderSoulFilter) o;
        return isDenyList == that.isDenyList &&
                shouldCompareTags == that.shouldCompareTags &&
                Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matches, isDenyList, shouldCompareTags);
    }
}
