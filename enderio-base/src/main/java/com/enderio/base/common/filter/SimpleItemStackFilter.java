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

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public record SimpleItemStackFilter(NonNullList<ItemStack> matches, boolean shouldCompareComponents, boolean isInverted) implements ItemStackFilter {

    public static SimpleItemStackFilter EMPTY = new SimpleItemStackFilter(List.of(), false, false);

    // TODO: 1.22: Change field names
    private static final Codec<SimpleItemStackFilter> NEW_CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("items")
                    .forGetter(SimpleItemStackFilter::matches),
                Codec.BOOL.fieldOf("isNbt").forGetter(SimpleItemStackFilter::shouldCompareComponents),
                Codec.BOOL.fieldOf("isInvert").forGetter(SimpleItemStackFilter::isInverted))
            .apply(componentInstance, SimpleItemStackFilter::new));

    // TODO: Remove in 1.22
    // The Codec used up to and including v7.0.2-alpha
    private static final Codec<SimpleItemStackFilter> LEGACY_CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                SimpleItemStackFilter.Slot.CODEC.sizeLimitedListOf(256)
                    .fieldOf("items")
                    .xmap(SimpleItemStackFilter::fromList, SimpleItemStackFilter::fromitems)
                    .forGetter(SimpleItemStackFilter::matches),
                Codec.BOOL.fieldOf("nbt").forGetter(SimpleItemStackFilter::shouldCompareComponents),
                Codec.BOOL.fieldOf("nbt").forGetter(SimpleItemStackFilter::isInverted))
            .apply(componentInstance, SimpleItemStackFilter::new));

    public static final Codec<SimpleItemStackFilter> CODEC = Codec.withAlternative(NEW_CODEC, LEGACY_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleItemStackFilter> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        SimpleItemStackFilter::matches,
        ByteBufCodecs.BOOL,
        SimpleItemStackFilter::shouldCompareComponents,
        ByteBufCodecs.BOOL,
        SimpleItemStackFilter::isInverted,
        SimpleItemStackFilter::new);

    public SimpleItemStackFilter(int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), false, false);
    }

    public int size() {
        return matches.size();
    }
    
    @Override
    public boolean test(@Nullable IItemHandler target, ItemStack itemStack) {
        for (var match : matches) {
            if (match.isEmpty()) {
                continue;
            }

            boolean matches = shouldCompareComponents
                ? ItemStack.isSameItemSameComponents(match, itemStack)
                : ItemStack.isSameItem(match, itemStack);
            
            if (matches) {
                return !isInverted;
            }
        }
        
        return isInverted;
    }

    // TODO: Remove in 1.22
    // region Legacy Serialization

    private static List<Slot> fromitems(List<ItemStack> items) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            slots.add(new Slot(i, items.get(i)));
        }
        return slots;
    }

    private static List<ItemStack> fromList(List<Slot> slots) {
        OptionalInt optionalint = slots.stream().mapToInt(Slot::index).max();
        if (optionalint.isEmpty()) {
            return List.of();
        }
        List<ItemStack> items = NonNullList.withSize(optionalint.getAsInt() + 1, ItemStack.EMPTY);
        for (Slot slot : slots) {
            items.set(slot.index, slot.item);
        }
        return items;
    }

    public record Slot(int index, ItemStack item) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
            p_331695_ -> p_331695_.group(
                    Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                    ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(Slot::item)
                )
                .apply(p_331695_, Slot::new)
        );
    }

    // endregion
}
