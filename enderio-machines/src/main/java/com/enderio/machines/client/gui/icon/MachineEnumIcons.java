package com.enderio.machines.client.gui.icon;

import com.enderio.EnderIOBase;
import com.enderio.core.client.icon.EnumIconMap;
import com.enderio.machines.common.blocks.alloy.AlloySmelterMode;
import com.enderio.machines.common.blocks.base.state.MachineStateType;

public class MachineEnumIcons {

    public static final EnumIconMap<AlloySmelterMode> ALLOY_SMELTER_MODE = createAll(AlloySmelterMode.class,
            "alloy_smelter_mode");
    public static final EnumIconMap<MachineStateType> MACHINE_STATE_TYPE = createAll(MachineStateType.class,
            "machine_state_type");
    public static final EnumIconMap<MachineStateType> NEW_MACHINE_STATE_TYPE = createAll(MachineStateType.class,
            "machine_state_type_new");

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIOBase.REGISTRY_NAMESPACE, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIOBase.REGISTRY_NAMESPACE, enumClass, iconFolder);
    }
}
