package com.enderio.base.common.lang;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class EIOEnumLang {
    public static EnumTranslationHolder<RedstoneControl> REDSTONE_CONTROL = new EnumTranslationHolder.Builder<>(RedstoneControl.class, "redstone")
        .addTranslation(RedstoneControl.ALWAYS_ACTIVE, "Always Active")
        .addTranslation(RedstoneControl.ACTIVE_WITH_SIGNAL, "Active with Signal")
        .addTranslation(RedstoneControl.ACTIVE_WITHOUT_SIGNAL, "Active without Signal")
        .addTranslation(RedstoneControl.NEVER_ACTIVE, "Never Active")
        .build();

    public static EnumTranslationHolder<GlassCollisionPredicate> GLASS_COLLISION = new EnumTranslationHolder.Builder<>(GlassCollisionPredicate.class, "collision")
        .addTranslation(GlassCollisionPredicate.PLAYERS_PASS, "Not solid to players")
        .addTranslation(GlassCollisionPredicate.PLAYERS_BLOCK, "Only solid to players")
        .addTranslation(GlassCollisionPredicate.MOBS_PASS, "Not solid to monsters")
        .addTranslation(GlassCollisionPredicate.MOBS_BLOCK, "Only solid to monsters")
        .addTranslation(GlassCollisionPredicate.ANIMALS_PASS, "Not solid to animals")
        .addTranslation(GlassCollisionPredicate.ANIMALS_BLOCK, "Only solid to animals")
        .build();

    public static void register() {
    }

    public static class EnumTranslationHolder<T extends Enum<T>> {
        private final EnumMap<T, Component> translations;

        private EnumTranslationHolder(EnumMap<T, Component> translations) {
            this.translations = translations;
        }

        @Nullable
        public Component get(T value) {
            return translations.get(value);
        }

        public static class Builder<T extends Enum<T>> {
            private final String translationPrefix;

            private final EnumMap<T, Component> translations;

            public Builder(Class<T> enumClass, String translationPrefix) {
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
                return new EnumTranslationHolder<>(translations);
            }
        }
    }
}
