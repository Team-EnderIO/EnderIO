package com.enderio.machines.common.lang;

import com.enderio.base.api.EnderIO;
import com.enderio.core.common.lang.EnumTranslationMap;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blocks.alloy.AlloySmelterMode;
import com.enderio.machines.common.blocks.powered_spawner.PoweredSpawnerMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MachineEnumLang {
    public static final EnumTranslationMap<AlloySmelterMode> ALLOY_SMELTER_MODE = builder(AlloySmelterMode.class,
            "alloy_smelter_mode").addTranslation(AlloySmelterMode.ALL, "Alloying and Smelting")
                    .addTranslation(AlloySmelterMode.ALLOYS, "Alloys Only")
                    .addTranslation(AlloySmelterMode.FURNACE, "Furnace Only")
                    .build();
    public static final EnumTranslationMap<PoweredSpawnerMode> POWERED_SPAWNER_MODE = builder(PoweredSpawnerMode.class,
            "powered_spawner_mode").addTranslation(PoweredSpawnerMode.SPAWN, "Spawn Mobs")
                    .addTranslation(PoweredSpawnerMode.CAPTURE, "Capture Mobs")
                    .build();

    private static <T extends Enum<T>> EnumTranslationMap.Builder<T> builder(Class<T> enumClass, String prefix) {
        return new EnumTranslationMap.Builder<>(EnderIO.NAMESPACE, MachineEnumLang::addTranslation, enumClass, prefix);
    }

    private static Component addTranslation(String prefix, ResourceLocation key, String english) {
        // TODO: Regilite should support a plain string key
        return EnderIOMachines.REGILITE.addTranslation(prefix, key, english);
    }

    public static void register() {
    }
}
