package com.enderio.base.common.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
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

public record EnderItemStackFilter(NonNullList<ItemStack> matches, boolean isDenyList, boolean shouldCompareComponents, DamageFilterMode damageFilterMode) implements ItemStackFilter {

    public static EnderItemStackFilter EMPTY = new EnderItemStackFilter(NonNullList.of(ItemStack.EMPTY), false, false, DamageFilterMode.IGNORE);

    // TODO: 1.22: Change field names
    public static final Codec<EnderItemStackFilter> CODEC = RecordCodecBuilder.create(
        componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("items")
                    .forGetter(EnderItemStackFilter::matches),
                Codec.BOOL.optionalFieldOf("isNbt", false).forGetter(EnderItemStackFilter::shouldCompareComponents),
                Codec.BOOL.optionalFieldOf("isInvert", false).forGetter(EnderItemStackFilter::isDenyList),
                DamageFilterMode.CODEC.optionalFieldOf("damageMode", DamageFilterMode.IGNORE).forGetter(EnderItemStackFilter::damageFilterMode))
            .apply(componentInstance, EnderItemStackFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnderItemStackFilter> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderItemStackFilter::matches,
        ByteBufCodecs.BOOL,
        EnderItemStackFilter::isDenyList,
        ByteBufCodecs.BOOL,
        EnderItemStackFilter::shouldCompareComponents,
        DamageFilterMode.STREAM_CODEC,
        EnderItemStackFilter::damageFilterMode,
        EnderItemStackFilter::new);

    public EnderItemStackFilter(int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), false, false, DamageFilterMode.IGNORE);
    }

    public EnderItemStackFilter(int size, boolean isDenyList, boolean shouldCompareComponents, DamageFilterMode damageFilterMode) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), isDenyList, shouldCompareComponents, damageFilterMode);
    }

    public EnderItemStackFilter(List<ItemStack> matches, boolean isDenyList, boolean shouldCompareComponents, DamageFilterMode damageFilterMode) {
        this(NonNullList.withSize(matches.size(), ItemStack.EMPTY), isDenyList, shouldCompareComponents, damageFilterMode);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }

    public int size() {
        return matches.size();
    }
    
    @Override
    public ItemStack test(@Nullable IItemHandler target, ItemStack itemStack) {
        if (!damageFilterMode.test(itemStack)) {
            return ItemStack.EMPTY;
        }

        for (var match : matches) {
            if (match.isEmpty()) {
                continue;
            }

            if (ItemStack.isSameItem(match, itemStack)) {
                if (!shouldCompareComponents || componentsMatch(match, itemStack)) {
                    return isDenyList ? ItemStack.EMPTY : itemStack;
                }
            }
        }
        
        return isDenyList ? itemStack : ItemStack.EMPTY;
    }

    // Ignore damage as it is controlled with the damage filter.
    private static final List<DataComponentType<?>> IGNORED_COMPONENT_TYPES = List.of(DataComponents.DAMAGE);

    private boolean componentsMatch(ItemStack referenceStack, ItemStack stack) {
        for (var component : referenceStack.getComponents()) {
            if (IGNORED_COMPONENT_TYPES.contains(component.type())) {
                continue;
            }

            if (!Objects.equals(stack.get(component.type()), component.value())) {
                return false;
            }
        }

        return true;
    }
}
