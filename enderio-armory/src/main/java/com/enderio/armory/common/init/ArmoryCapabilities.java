package com.enderio.armory.common.init;

import com.enderio.EnderIOBase;
import com.enderio.armory.api.capability.IDarkSteelUpgradable;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ArmoryCapabilities {
    public static final class DarkSteelUpgradable {
        public static final ItemCapability<IDarkSteelUpgradable, Void> ITEM =
            ItemCapability.createVoid(
                EnderIOBase.loc("dark_steel_upgradable"),
                IDarkSteelUpgradable.class);
    }
}
