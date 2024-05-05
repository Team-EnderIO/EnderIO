package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.api.capability.IDarkSteelUpgradable;
import com.enderio.api.capability.ISideConfig;
import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class EIOCapabilities {

    public static final class CapacitorData {
        public static final ItemCapability<ICapacitorData, Void> ITEM =
            ItemCapability.createVoid(
                EnderIO.loc("capacitor_data"),
                ICapacitorData.class);
    }

    public static final class DarkSteelUpgradable {
        public static final ItemCapability<IDarkSteelUpgradable, Void> ITEM =
            ItemCapability.createVoid(
                EnderIO.loc("dark_steel_upgradable"),
                IDarkSteelUpgradable.class);
    }

    public static final class SideConfig {
        public static final BlockCapability<ISideConfig, Direction> BLOCK =
            BlockCapability.createSided(
                EnderIO.loc("side_config"),
                ISideConfig.class);
    }
}
