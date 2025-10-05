package com.enderio.enderio.client.foundation.icon;

import com.enderio.core.client.icon.EnumIconMap;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
import com.enderio.enderio.foundation.state.MachineStateType;

public class MachineEnumIcons {

    public static final EnumIconMap<AlloySmelterMode> ALLOY_SMELTER_MODE = createAll(AlloySmelterMode.class,
            "alloy_smelter_mode");
    public static final EnumIconMap<PoweredSpawnerMode> POWERED_SPAWNER_MODE = createAll(PoweredSpawnerMode.class,
            "powered_spawner_mode");
    public static final EnumIconMap<MachineStateType> MACHINE_STATE_TYPE = createAll(MachineStateType.class,
            "machine_state_type");
    public static final EnumIconMap<MachineStateType> NEW_MACHINE_STATE_TYPE = createAll(MachineStateType.class,
            "machine_state_type_new");

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIO.MOD_ID, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIO.MOD_ID, enumClass, iconFolder);
    }
}
