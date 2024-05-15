package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.capability.IConduitUpgrade;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ConduitCapabilities {

    public static final class ConduitUpgrade {
        public static final ItemCapability<IConduitUpgrade, Void> ITEM =
            ItemCapability.createVoid(
                EnderIO.loc("conduit_upgrade"),
                IConduitUpgrade.class);
    }
}
