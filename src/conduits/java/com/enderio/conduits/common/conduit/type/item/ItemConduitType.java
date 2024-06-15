package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ItemStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ItemConduitType extends ConduitType<ItemConduitData> {
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    @Override
    public ConduitTicker<ItemConduitData> getTicker() {
        return ItemConduitTicker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public ItemConduitData createConduitData(Level level, BlockPos pos) {
        return new ItemConduitData();
    }

    @Override
    public boolean canApplyUpgrade(ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ItemSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(ResourceFilter resourceFilter) {
        return resourceFilter instanceof ItemStackFilter;
    }
}
