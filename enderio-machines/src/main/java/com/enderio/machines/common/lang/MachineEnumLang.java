package com.enderio.machines.common.lang;

import com.enderio.EnderIOBase;
import com.enderio.core.common.lang.EnumTranslationMap;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blocks.alloy.AlloySmelterMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MachineEnumLang {
    public static final EnumTranslationMap<AlloySmelterMode> ALLOY_SMELTER_MODE = builder(AlloySmelterMode.class,
            "alloy_smelter_mode").addTranslation(AlloySmelterMode.ALL, "Alloying and Smelting")
                    .addTranslation(AlloySmelterMode.ALLOYS, "Alloys Only")
                    .addTranslation(AlloySmelterMode.FURNACE, "Furnace Only")
                    .build();

    private static <T extends Enum<T>> EnumTranslationMap.Builder<T> builder(Class<T> enumClass, String prefix) {
        return new EnumTranslationMap.Builder<>(EnderIOBase.REGISTRY_NAMESPACE, MachineEnumLang::addTranslation,
                enumClass, prefix);
    }

    private static Component addTranslation(String prefix, ResourceLocation key, String english) {
        // TODO: Regilite should support a plain string key
        return EnderIOMachines.REGILITE.addTranslation(prefix, key, english);
    }

    public static void register() {
    }
}
