package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;

public class RedstoneConduitType extends SimpleConduitType<RedstoneConduitData> {

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    public RedstoneConduitType() {
        super(new RedstoneConduitTicker(), RedstoneConduitData::new, MENU_DATA);
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return switch (slotType) {
            case FILTER_EXTRACT -> resourceFilter instanceof RedstoneExtractFilter;
            case FILTER_INSERT -> resourceFilter instanceof RedstoneInsertFilter;
            default -> false;
        };
    }
}
