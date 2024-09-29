package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.api.filter.ItemStackFilter;
import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.SlotType;
import com.enderio.conduits.api.upgrade.ConduitUpgrade;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ItemConduit(
    ResourceLocation texture,
    Component description
) implements Conduit<ItemConduit> {

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    @Override
    public ConduitType<ItemConduit> type() {
        return ConduitTypes.ITEM.get();
    }

    @Override
    public ItemConduitTicker getTicker() {
        return ItemConduitTicker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return conduitUpgrade instanceof ExtractionSpeedUpgrade;
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return resourceFilter instanceof ItemStackFilter;
    }

    @Override
    public int compareTo(@NotNull ItemConduit o) {
        return 0;
    }
}
