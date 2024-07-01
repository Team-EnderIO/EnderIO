package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitTypeSerializer;
import com.enderio.api.conduit.NewConduitTypeSerializer;
import com.enderio.api.conduit.SimpleConduitNetworkType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ItemStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.EIOConduitTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemConduitType(
    ResourceLocation texture,
    Component description
) implements SimpleConduitNetworkType<ItemConduitType, ItemConduitData> {

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, true, true, true, true);

    @Override
    public NewConduitTypeSerializer<ItemConduitType> serializer() {
        return EIOConduitTypes.TypeSerializers.ITEM.get();
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
    public ItemConduitData createConduitData(Level level, BlockPos pos) {
        return new ItemConduitData();
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
    public int compareTo(@NotNull ItemConduitType o) {
        return 0;
    }
}
