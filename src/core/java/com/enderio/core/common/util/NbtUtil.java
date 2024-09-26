package com.enderio.core.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.function.Supplier;

public class NbtUtil {
    // Ensures the list is exactly the right length.
    public static void ensureSize(ListTag list, int size, Supplier<CompoundTag> defaultValue) {
        if (list.size() == size) {
            return;
        }

        while (list.size() < size) {
            list.add(defaultValue.get());
        }

        while (list.size() > size) {
            list.remove(list.size() - 1);
        }
    }
}
