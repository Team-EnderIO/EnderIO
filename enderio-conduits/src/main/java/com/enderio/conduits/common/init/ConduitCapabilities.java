package com.enderio.conduits.common.init;

import com.enderio.EnderIOBase;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ConduitCapabilities {

    public static final class ConduitUpgrade {
        public static final ItemCapability<com.enderio.conduits.api.upgrade.ConduitUpgrade, Void> ITEM =
            ItemCapability.createVoid(
                EnderIOBase.loc("conduit_upgrade"),
                com.enderio.conduits.api.upgrade.ConduitUpgrade.class);
    }
}
