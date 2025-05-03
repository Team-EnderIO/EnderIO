package com.enderio.base.common.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// This is just an example as to why we needed a more extensive filter interface :)
public record ExistingItemStackFilter(boolean hasSnapshot, NonNullList<ItemStack> snapshot, boolean shouldCompareComponents, boolean isInverted) implements ItemStackFilter {
    public static final Codec<ExistingItemStackFilter> CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                Codec.BOOL.fieldOf("hasSnapshot").forGetter(ExistingItemStackFilter::hasSnapshot),
                OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("snapshot")
                    .forGetter(ExistingItemStackFilter::snapshot),
                Codec.BOOL.fieldOf("shouldCompareComponents").forGetter(ExistingItemStackFilter::shouldCompareComponents),
                Codec.BOOL.fieldOf("isDenyList").forGetter(ExistingItemStackFilter::isInverted))
            .apply(componentInstance, ExistingItemStackFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExistingItemStackFilter> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ExistingItemStackFilter::hasSnapshot,
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        ExistingItemStackFilter::snapshot,
        ByteBufCodecs.BOOL,
        ExistingItemStackFilter::shouldCompareComponents,
        ByteBufCodecs.BOOL,
        ExistingItemStackFilter::isInverted,
        ExistingItemStackFilter::new);

    public ExistingItemStackFilter(boolean hasSnapshot, List<ItemStack> snapshot, boolean shouldCompareComponents, boolean isInverted) {
        this(hasSnapshot, NonNullList.withSize(snapshot.size(), ItemStack.EMPTY), shouldCompareComponents, isInverted);

        for (int i = 0; i < snapshot.size(); i++) {
            this.snapshot.set(i, snapshot.get(i));
        }
    }
    
    @Override
    public ItemStack test(@Nullable IItemHandler target, ItemStack stack) {
        if (hasSnapshot) {
            for (var match : snapshot) {
                if (match.isEmpty()) {
                    continue;
                }

                boolean matches = shouldCompareComponents
                    ? ItemStack.isSameItemSameComponents(match, stack)
                    : ItemStack.isSameItem(match, stack);

                if (matches) {
                    return isInverted ? ItemStack.EMPTY : stack;
                }
            }
        } else if (target != null) {
            for (int i = 0; i < target.getSlots(); i++) {
                ItemStack match = target.getStackInSlot(i);
                if (match.isEmpty()) {
                    continue;
                }

                boolean matches = shouldCompareComponents
                    ? ItemStack.isSameItemSameComponents(match, stack)
                    : ItemStack.isSameItem(match, stack);

                if (matches) {
                    return isInverted ? ItemStack.EMPTY : stack;
                }
            }
        }

        return isInverted ? stack : ItemStack.EMPTY;
    }
}
