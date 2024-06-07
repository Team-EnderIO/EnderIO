package com.enderio.conduits.common.integrations.cctweaked;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;

public class CCRedstoneUpgrade implements ConduitUpgrade {

    public static final CCRedstoneUpgrade INSTANCE = new CCRedstoneUpgrade();

    @Override
    public boolean equals(Object obj) {
        return INSTANCE == obj;
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
