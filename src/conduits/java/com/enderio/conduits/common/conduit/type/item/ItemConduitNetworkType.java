package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.SimpleConduitNetworkType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ItemStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ItemConduitNetworkType implements SimpleConduitNetworkType<ItemConduitData> {

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    @Override
    public ItemConduitTicker getTicker() {
        return ItemConduitTicker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData(Void unused) {
        return MENU_DATA;
    }

    @Override
    public ItemConduitData createConduitData(Void unused, Level level, BlockPos pos) {
        return new ItemConduitData();
    }

    @Override
    public boolean canApplyUpgrade(Void unused, SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(Void unused, SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof ItemStackFilter;
    }
}
