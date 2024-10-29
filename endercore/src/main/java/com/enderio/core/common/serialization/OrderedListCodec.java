package com.enderio.core.common.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.NonNullList;

/**
 * Ensures the serialized list retains it's order.
 * WARNING: Editing this class *will* mess up saves.
 * If you're still sure you need to, remember to use {@link Codec#withAlternative(Codec, Codec)} to provide backwards-compatibility.
 */
public class OrderedListCodec {

    public static <T> Codec<List<T>> create(Codec<T> itemCodec, T defaultValue) {
        return create(Integer.MAX_VALUE, itemCodec, defaultValue);
    }

    public static <T> Codec<List<T>> create(int maxSize, Codec<T> itemCodec, T defaultValue) {
        return createItemCodec(itemCodec, maxSize).sizeLimitedListOf(maxSize)
                .xmap(list -> fromItems(list, defaultValue), OrderedListCodec::toItems);
    }

    private static <T> Codec<Item<T>> createItemCodec(Codec<T> itemCodec, int maxSize) {
        // @formatter:off
        return RecordCodecBuilder.create(inst -> inst
            .group(
                Codec.intRange(0, maxSize - 1).fieldOf("index").forGetter(Item::index),
                itemCodec.fieldOf("value").forGetter(Item::value)
            ).apply(inst, Item::new));
        // @formatter:on
    }

    private static <T> List<Item<T>> toItems(List<T> values) {
        List<Item<T>> items = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            items.add(new Item<>(i, values.get(i)));
        }

        return items;
    }

    private static <T> List<T> fromItems(List<Item<T>> slots, T defaultValue) {
        OptionalInt optionalint = slots.stream().mapToInt(Item::index).max();
        if (optionalint.isEmpty()) {
            return List.of();
        }

        List<T> fluids = NonNullList.withSize(optionalint.getAsInt() + 1, defaultValue);
        for (Item<T> slot : slots) {
            fluids.set(slot.index, slot.value);
        }
        return fluids;
    }

    private record Item<T>(int index, T value) {
    }
}
