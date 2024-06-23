package com.enderio.machines.client.icon;

import com.enderio.EnderIO;
import com.enderio.core.client.icon.EnumIconMap;
import com.enderio.machines.common.blockentity.AlloySmelterMode;

public class MachineEnumIcons {

    public static final EnumIconMap<AlloySmelterMode> ALLOY_SMELTER_MODE = createAll(AlloySmelterMode.class, "alloy_smelter_mode");

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIO.MODID, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIO.MODID, enumClass, iconFolder);
    }
}
