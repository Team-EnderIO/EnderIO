package com.enderio.conduits.common.init;

import com.enderio.EnderIOBase;
import com.enderio.conduits.common.conduit.facades.ConduitFacadeProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ConduitCapabilities {

    public static final class ConduitUpgrade {
        public static final ItemCapability<com.enderio.conduits.api.upgrade.ConduitUpgrade, Void> ITEM =
            ItemCapability.createVoid(
                EnderIOBase.loc("conduit_upgrade"),
                com.enderio.conduits.api.upgrade.ConduitUpgrade.class);
    }

    public static final class ConduitFacade {
        public static final ItemCapability<ConduitFacadeProvider, Void> ITEM =
            ItemCapability.createVoid(
                EnderIOBase.loc("conduit_facade"),
                ConduitFacadeProvider.class);
    }
}
