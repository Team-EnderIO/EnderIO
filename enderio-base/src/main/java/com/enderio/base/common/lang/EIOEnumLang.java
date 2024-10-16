package com.enderio.base.common.lang;

import com.enderio.EnderIOBase;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.core.common.lang.EnumTranslationMap;
import com.enderio.regilite.Regilite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EIOEnumLang {

    private static final Regilite REGILITE = EnderIOBase.REGILITE;

    public static final EnumTranslationMap<RedstoneControl> REDSTONE_CONTROL = builder(RedstoneControl.class, "redstone")
        .addTranslation(RedstoneControl.ALWAYS_ACTIVE, "Always Active")
        .addTranslation(RedstoneControl.ACTIVE_WITH_SIGNAL, "Active with Signal")
        .addTranslation(RedstoneControl.ACTIVE_WITHOUT_SIGNAL, "Active without Signal")
        .addTranslation(RedstoneControl.NEVER_ACTIVE, "Never Active")
        .build();

    public static final EnumTranslationMap<GlassCollisionPredicate> GLASS_COLLISION = builder(GlassCollisionPredicate.class, "collision")
        .addTranslation(GlassCollisionPredicate.PLAYERS_PASS, "Not solid to players")
        .addTranslation(GlassCollisionPredicate.PLAYERS_BLOCK, "Only solid to players")
        .addTranslation(GlassCollisionPredicate.MOBS_PASS, "Not solid to monsters")
        .addTranslation(GlassCollisionPredicate.MOBS_BLOCK, "Only solid to monsters")
        .addTranslation(GlassCollisionPredicate.ANIMALS_PASS, "Not solid to animals")
        .addTranslation(GlassCollisionPredicate.ANIMALS_BLOCK, "Only solid to animals")
        .build();

    private static <T extends Enum<T>> EnumTranslationMap.Builder<T> builder(Class<T> enumClass, String prefix) {
        return new EnumTranslationMap.Builder<>(EnderIOBase.REGISTRY_NAMESPACE, EIOEnumLang::addTranslation, enumClass, prefix);
    }

    private static Component addTranslation(String prefix, ResourceLocation key, String english) {
        return REGILITE.lang().add(prefix, key, english);
    }

    public static void register() {
    }
}
