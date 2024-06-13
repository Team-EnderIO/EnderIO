package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;

public class RedstoneConduitType extends SimpleConduitType<RedstoneConduitData> {

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    public RedstoneConduitType() {
        super(new RedstoneConduitTicker(), RedstoneConduitData::new, MENU_DATA);
    }
}
