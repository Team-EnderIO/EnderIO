package com.enderio.enderio.content.filters.item.limited;

import com.enderio.core.common.serialization.OrderedListCodec;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.content.filters.item.ItemFilterUtils;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A filter that limits transfer of items so a connected inventory always maintains
 * the configured stock level for each item. The count of each entry's ItemStack
 * represents the desired inventory stock level (the "limit").
 *
 * <p>When used as an insert filter: allows insertion until the target inventory
 * has {@code limit} of that item.
 * When used as an extract filter: allows extraction until the source inventory
 * retains {@code limit} of that item.
 */
public record LimitedItemFilter(NonNullList<ItemStack> matches, boolean shouldCompareComponents,
        DamageFilterMode damageFilterMode) implements ItemFilter {

    public static final int SLOT_COUNT = 18;

    public static final LimitedItemFilter EMPTY = new LimitedItemFilter(SLOT_COUNT);

    public static final Codec<LimitedItemFilter> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("items")
                    .forGetter(LimitedItemFilter::matches),
                    Codec.BOOL.optionalFieldOf("isNbt", false).forGetter(LimitedItemFilter::shouldCompareComponents),
                    DamageFilterMode.CODEC.optionalFieldOf("damageMode", DamageFilterMode.IGNORE)
                            .forGetter(LimitedItemFilter::damageFilterMode))
            .apply(instance, LimitedItemFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LimitedItemFilter> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)), LimitedItemFilter::matches,
            ByteBufCodecs.BOOL, LimitedItemFilter::shouldCompareComponents,
            DamageFilterMode.STREAM_CODEC, LimitedItemFilter::damageFilterMode,
            LimitedItemFilter::new);

    public LimitedItemFilter(int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), false, DamageFilterMode.IGNORE);
    }

    public LimitedItemFilter(List<ItemStack> matches, boolean shouldCompareComponents,
            DamageFilterMode damageFilterMode) {
        this(NonNullList.withSize(matches.size(), ItemStack.EMPTY), shouldCompareComponents, damageFilterMode);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }

    @Override
    public ItemStack test(@Nullable IItemHandler target, ItemStack stack) {
        if (!damageFilterMode.test(stack)) {
            return ItemStack.EMPTY;
        }

        for (var match : matches) {
            if (match.isEmpty()) {
                continue;
            }

            if (!ItemStack.isSameItem(match, stack)) {
                continue;
            }

            if (shouldCompareComponents && !ItemFilterUtils.doComponentsMatch(match, stack)) {
                continue;
            }

            if (target == null) {
                return stack;
            }

            // The match stack's count encodes the desired stock level
            int limit = match.getCount();

            // Count how many matching items are currently in the target
            int currentCount = 0;
            for (int i = 0; i < target.getSlots(); i++) {
                ItemStack inSlot = target.getStackInSlot(i);
                if (!inSlot.isEmpty() && ItemStack.isSameItem(match, inSlot)) {
                    if (!shouldCompareComponents || ItemFilterUtils.doComponentsMatch(match, inSlot)) {
                        currentCount += inSlot.getCount();
                    }
                }
            }

            int available = limit - currentCount;
            if (available <= 0) {
                return ItemStack.EMPTY;
            }

            return stack.copyWithCount(Math.min(stack.getCount(), available));
        }

        // Whitelist only: deny items not found in filter
        return ItemStack.EMPTY;
    }
}
