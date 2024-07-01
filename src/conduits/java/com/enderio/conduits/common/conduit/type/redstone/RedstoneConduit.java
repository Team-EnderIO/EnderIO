package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SimpleConduit;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.init.Conduits;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record RedstoneConduit(
    ResourceLocation texture,
    Component description
) implements SimpleConduit<RedstoneConduit, RedstoneConduitData> {

    private static final RedstoneConduitTicker TICKER = new RedstoneConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    @Override
    public ConduitType<RedstoneConduit> type() {
        return ConduitTypes.REDSTONE.get();
    }

    @Override
    public RedstoneConduitTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public RedstoneConduitData createConduitData(Level level, BlockPos pos) {
        return new RedstoneConduitData();
    }

    @Override
    public boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return switch (slotType) {
            case FILTER_EXTRACT -> resourceFilter instanceof RedstoneExtractFilter;
            case FILTER_INSERT -> resourceFilter instanceof RedstoneInsertFilter;
            default -> false;
        };
    }

    @Override
    public int compareTo(@NotNull RedstoneConduit o) {
        return 0;
    }
}
