package com.enderio.core.common.backports;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Backwards compatibility shim for item data components.
 * @param <T>
 */
public class DataComponentType<T> {
    private final Function<CompoundTag, T> itemGetter;
    private final BiConsumer<CompoundTag, @Nullable T> itemSetter;

    public DataComponentType(Function<CompoundTag, T> itemGetter, BiConsumer<CompoundTag, @Nullable T> itemSetter) {
        this.itemGetter = itemGetter;
        this.itemSetter = itemSetter;
    }

    public boolean has(ItemStack itemStack) {
        return get(itemStack) == null;
    }

    @Nullable
    public T get(ItemStack itemStack) {
        var tag = itemStack.getTag();
        if (tag == null) {
            return null;
        }

        return itemGetter.apply(tag);
    }

    public T getOrDefault(ItemStack itemStack, @Nullable T defaultValue) {
        @Nullable T value = get(itemStack);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public void set(ItemStack itemStack, T value) {
        var tag = itemStack.getOrCreateTag();
        itemSetter.accept(tag, value);
    }
}
