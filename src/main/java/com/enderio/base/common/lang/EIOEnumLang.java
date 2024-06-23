package com.enderio.base.common.lang;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;

public class EIOEnumLang {
    public static EnumTranslationHolder<RedstoneControl> REDSTONE_CONTROL = new EnumTranslationHolder.Builder<>(RedstoneControl.class, "redstone")
        .addTranslation(RedstoneControl.ALWAYS_ACTIVE, "Always Active")
        .addTranslation(RedstoneControl.ACTIVE_WITH_SIGNAL, "Active with Signal")
        .addTranslation(RedstoneControl.ACTIVE_WITHOUT_SIGNAL, "Active without Signal")
        .addTranslation(RedstoneControl.NEVER_ACTIVE, "Never Active")
        .build();

    public static void register() {
    }

    public static class EnumTranslationHolder<T extends Enum<T>> {
        private final EnumMap<T, Component> translations;

        public EnumTranslationHolder(Class<T> enumClass, EnumMap<T, Component> translations) {
            this.translations = translations;
        }

        public Component get(T value) {
            Component component = translations.get(value);
            if (component == null) {
                throw new UnsupportedOperationException("No translation found for " + value);
            }

            return component;
        }

        public static class Builder<T extends Enum<T>> {
            private final Class<T> enumClass;
            private final String translationPrefix;

            private final EnumMap<T, Component> translations;

            public Builder(Class<T> enumClass, String translationPrefix) {
                this.enumClass = enumClass;
                this.translationPrefix = translationPrefix;
                translations = new EnumMap<>(enumClass);
            }

            public Builder<T> put(T value, Component translation) {
                translations.put(value, translation);
                return this;
            }

            public Builder<T> addTranslation(T value, String english) {
                translations.put(value, EnderIO.getRegilite()
                    .addTranslation("gui", EnderIO.loc(translationPrefix + "." + value.name().toLowerCase()), english));
                return this;
            }

            public EnumTranslationHolder<T> build() {
                return new EnumTranslationHolder<>(enumClass, translations);
            }
        }
    }
}
