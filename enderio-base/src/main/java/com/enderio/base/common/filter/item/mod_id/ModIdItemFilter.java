package com.enderio.base.common.filter.item.mod_id;

import com.enderio.base.api.filter.ItemFilter;
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

public record ModIdItemFilter(NonNullList<ItemStack> examples, boolean isDenyList) implements ItemFilter {
    public static final Codec<ModIdItemFilter> CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(OrderedListCodec.create(256, ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
                    .fieldOf("examples")
                    .forGetter(ModIdItemFilter::examples),
                    Codec.BOOL.fieldOf("isDenyList").forGetter(ModIdItemFilter::isDenyList))
            .apply(componentInstance, ModIdItemFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModIdItemFilter> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)), ModIdItemFilter::examples,
            ByteBufCodecs.BOOL, ModIdItemFilter::isDenyList, ModIdItemFilter::new);

    public ModIdItemFilter(List<ItemStack> examples, boolean isDenyList) {
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
