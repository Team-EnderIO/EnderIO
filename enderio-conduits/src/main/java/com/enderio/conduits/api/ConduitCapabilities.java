package com.enderio.conduits.api;

import com.enderio.base.api.EnderIO;
import com.enderio.conduits.api.facade.ConduitFacadeProvider;
import com.enderio.conduits.api.upgrade.ConduitUpgrade;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ConduitCapabilities {
    public static final ItemCapability<ConduitUpgrade, Void> CONDUIT_UPGRADE = ItemCapability
            .createVoid(EnderIO.loc("conduit_upgrade"), ConduitUpgrade.class);
    public static final ItemCapability<ConduitFacadeProvider, Void> CONDUIT_FACADE_PROVIDER = ItemCapability
            .createVoid(EnderIO.loc("conduit_facade_provider"), ConduitFacadeProvider.class);
}
