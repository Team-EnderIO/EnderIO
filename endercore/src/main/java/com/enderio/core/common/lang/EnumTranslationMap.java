package com.enderio.core.common.lang;

import java.util.EnumMap;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EnumTranslationMap<T extends Enum<T>> {
    private final EnumMap<T, Component> translations;

    private EnumTranslationMap(EnumMap<T, Component> translations) {
        this.translations = translations;
    }

    // TODO Get enum name? useful for labels?

    @Nullable
    public Component get(T value) {
        return translations.get(value);
    }

    public static class Builder<T extends Enum<T>> {

        @FunctionalInterface
        public interface TranslationRegistrar {
            Component createTranslation(String prefix, ResourceLocation key, String english);
        }

        private final String modId;
        private final TranslationRegistrar translationRegistrar;
        private final String translationPrefix;

        private final EnumMap<T, Component> translations;

        public Builder(String modId, TranslationRegistrar translationRegistrar, Class<T> enumClass,
                String translationPrefix) {
            this.modId = modId;
            this.translationPrefix = translationPrefix;
            this.translationRegistrar = translationRegistrar;
            translations = new EnumMap<>(enumClass);
        }

        public Builder<T> addTranslation(T value, String english) {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(modId,
                    translationPrefix + "." + value.name().toLowerCase(Locale.ROOT));
            translations.put(value, translationRegistrar.createTranslation("gui", key, english));

            return this;
        }

        public EnumTranslationMap<T> build() {
            return new EnumTranslationMap<>(new EnumMap<>(translations));
        }
    }
}
