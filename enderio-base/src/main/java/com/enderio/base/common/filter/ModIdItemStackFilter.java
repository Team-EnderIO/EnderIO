package com.enderio.base.common.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public record ModIdItemStackFilter(NonNullList<ItemStack> examples, boolean isDenyList) implements ItemStackFilter {
    public static final Codec<ModIdItemStackFilter> CODEC = RecordCodecBuilder
            .create(componentInstance -> componentInstance
                    .group(OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                            .fieldOf("examples")
                            .forGetter(ModIdItemStackFilter::examples),
                            Codec.BOOL.fieldOf("isDenyList").forGetter(ModIdItemStackFilter::isDenyList))
                    .apply(componentInstance, ModIdItemStackFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModIdItemStackFilter> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)), ModIdItemStackFilter::examples,
            ByteBufCodecs.BOOL, ModIdItemStackFilter::isDenyList, ModIdItemStackFilter::new);

    public ModIdItemStackFilter(List<ItemStack> examples, boolean isDenyList) {
        this(NonNullList.withSize(examples.size(), ItemStack.EMPTY), isDenyList);

        for (int i = 0; i < examples.size(); i++) {
            this.examples.set(i, examples.get(i));
        }
    }

    @Override
    public ItemStack test(@Nullable IItemHandler target, ItemStack stack) {
        var testKey = BuiltInRegistries.ITEM.getKey(stack.getItem());

        for (var example : examples) {
            if (example.isEmpty()) {
                continue;
            }

            var exampleKey = BuiltInRegistries.ITEM.getKey(example.getItem());
            if (testKey.getNamespace().equals(exampleKey.getNamespace())) {
                return isDenyList ? ItemStack.EMPTY : stack;
            }
        }

        return isDenyList ? stack : ItemStack.EMPTY;
    }
}
