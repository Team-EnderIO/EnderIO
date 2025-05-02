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

public record SimpleItemStackFilter(NonNullList<ItemStack> matches, boolean shouldCompareComponents, boolean isInverted) implements ItemStackFilter {

    public static SimpleItemStackFilter EMPTY = new SimpleItemStackFilter(NonNullList.of(ItemStack.EMPTY), false, false);

    // TODO: 1.22: Change field names
    public static final Codec<SimpleItemStackFilter> CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("items")
                    .forGetter(SimpleItemStackFilter::matches),
                Codec.BOOL.fieldOf("isNbt").forGetter(SimpleItemStackFilter::shouldCompareComponents),
                Codec.BOOL.fieldOf("isInvert").forGetter(SimpleItemStackFilter::isInverted))
            .apply(componentInstance, SimpleItemStackFilter::new));

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

    public SimpleItemStackFilter(int size, boolean shouldCompareComponents, boolean isInverted) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), shouldCompareComponents, isInverted);
    }

    public SimpleItemStackFilter(List<ItemStack> matches, boolean shouldCompareComponents, boolean isInverted) {
        this(NonNullList.withSize(matches.size(), ItemStack.EMPTY), shouldCompareComponents, isInverted);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
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
}
