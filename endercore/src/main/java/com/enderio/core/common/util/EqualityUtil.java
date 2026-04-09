package com.enderio.core.common.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EqualityUtil {
    public static <T> boolean areListsEqual(@Nullable List<T> firstList, @Nullable List<T> secondList, BiFunction<T, T, Boolean> equalityFunction) {
        if (firstList == null && secondList == null) {
            return true;
        }

        if ((firstList == null) != (secondList == null)) {
            return false;
        }

        if (firstList.size() != secondList.size()) {
            return false;
        }

        for (int i = 0; i < firstList.size(); i++) {
            if (!equalityFunction.apply(firstList.get(i), secondList.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static <T> int hashListContents(List<T> list, Function<T, Integer> hashFunction) {
        int hashCode = 1;
        for (T e : list) {
            hashCode = 31 * hashCode + (e == null ? 0 : hashFunction.apply(e));
        }
        return hashCode;
    }
}
