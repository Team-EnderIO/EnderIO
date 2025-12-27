package com.enderio.core.client.icon;

import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.stream.Collectors;

// TODO: Use StringRepresentable instead of enum names.
public class EnumIconMap<T extends Enum<T>> {
    private final EnumMap<T, Identifier> icons;

    public EnumIconMap(String modId, Class<T> enumClass, String iconFolder) {
        // noinspection Convert2Diamond
        icons = new EnumMap<T, Identifier>(Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(e -> e, e -> createFor(modId, iconFolder, e))));
    }

    private EnumIconMap(EnumMap<T, Identifier> icons) {
        this.icons = icons;
    }

    @Nullable
    public Identifier get(T value) {
        return icons.get(value);
    }

    private static <T extends Enum<T>> Identifier createFor(String modId, String iconFolder, T value) {
        return Identifier.fromNamespaceAndPath(modId,
                "icon/" + iconFolder + "/" + value.name().toLowerCase(Locale.ROOT));
    }

    public static class Builder<T extends Enum<T>> {
        private final String modId;
        private final Class<T> enumClass;
        private final String iconFolder;

        private final EnumMap<T, Identifier> icons;

        public Builder(String modId, Class<T> enumClass, String iconFolder) {
            this.modId = modId;
            this.enumClass = enumClass;
            this.iconFolder = iconFolder;
            icons = new EnumMap<>(enumClass);
        }

        public Builder<T> add(T value) {
            icons.put(value, createFor(modId, iconFolder, value));
            return this;
        }

        public Builder<T> add(T value, Identifier icon) {
            icons.put(value, icon);
            return this;
        }

        public Builder<T> addAll() {
            for (T value : enumClass.getEnumConstants()) {
                icons.put(value, createFor(modId, iconFolder, value));
            }
            return this;
        }

        public Builder<T> remove(T value) {
            icons.remove(value);
            return this;
        }

        public EnumIconMap<T> build() {
            return new EnumIconMap<>(icons);
        }
    }
}
