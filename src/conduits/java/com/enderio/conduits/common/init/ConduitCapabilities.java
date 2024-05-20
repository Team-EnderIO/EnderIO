package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ConduitCapabilities {

    public static final class ConduitUpgrade {
        public static final ItemCapability<com.enderio.api.conduit.upgrade.ConduitUpgrade, Void> ITEM =
            ItemCapability.createVoid(
                EnderIO.loc("conduit_upgrade"),
                com.enderio.api.conduit.upgrade.ConduitUpgrade.class);
    }
}
