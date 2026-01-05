package com.enderio.core.common.lang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumLangMap<T extends Enum<T>> {
    private final Map<T, MutableComponent> components;

    @SafeVarargs
    public EnumLangMap(Class<T> clazz, String namespace, String path, T ...excluded) {
        var toExclude = List.of(excluded);

        this.components = Arrays.stream(clazz.getEnumConstants())
            .filter(v -> !toExclude.contains(v))
            .collect(Collectors.toMap(v -> v, v -> Component.translatable(namespace + "." + path + "." + v.name().toLowerCase(Locale.ROOT))));
    }

    @Nullable
    public MutableComponent get(T value) {
        return components.get(value);
    }
}
