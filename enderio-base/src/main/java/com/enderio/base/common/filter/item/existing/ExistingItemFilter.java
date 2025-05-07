package com.enderio.base.common.filter.item.existing;

import com.enderio.base.api.filter.ItemFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

// This is just an example as to why we needed a more extensive filter interface :)
public record ExistingItemFilter(boolean hasSnapshot, NonNullList<ItemStack> snapshot, boolean shouldCompareComponents,
        boolean isInverted) implements ItemFilter {

    public static final Codec<ExistingItemFilter> CODEC = RecordCodecBuilder
            .create(componentInstance -> componentInstance
                    .group(Codec.BOOL.fieldOf("hasSnapshot").forGetter(ExistingItemFilter::hasSnapshot),
                            OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                                    .fieldOf("snapshot")
                                    .forGetter(ExistingItemFilter::snapshot),
                            Codec.BOOL.fieldOf("shouldCompareComponents")
                                    .forGetter(ExistingItemFilter::shouldCompareComponents),
                            Codec.BOOL.fieldOf("isDenyList").forGetter(ExistingItemFilter::isInverted))
                    .apply(componentInstance, ExistingItemFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExistingItemFilter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ExistingItemFilter::hasSnapshot,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)), ExistingItemFilter::snapshot,
            ByteBufCodecs.BOOL, ExistingItemFilter::shouldCompareComponents, ByteBufCodecs.BOOL,
            ExistingItemFilter::isInverted, ExistingItemFilter::new);

    public ExistingItemFilter(boolean hasSnapshot, List<ItemStack> snapshot, boolean shouldCompareComponents,
            boolean isInverted) {
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

                boolean matches = shouldCompareComponents ? ItemStack.isSameItemSameComponents(match, stack)
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

                boolean matches = shouldCompareComponents ? ItemStack.isSameItemSameComponents(match, stack)
                        : ItemStack.isSameItem(match, stack);

                if (matches) {
                    return isInverted ? ItemStack.EMPTY : stack;
                }
            }
        }

        return isInverted ? stack : ItemStack.EMPTY;
    }
}
