package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.SimpleConduitNetworkType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RedstoneConduitNetworkType implements SimpleConduitNetworkType<RedstoneConduitData> {

    private static final RedstoneConduitTicker TICKER = new RedstoneConduitTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    @Override
    public ConduitTicker<Void, ConduitNetworkContext.Dummy, RedstoneConduitData> getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(Void unused) {
        return MENU_DATA;
    }

    @Override
    public RedstoneConduitData createConduitData(Void unused, Level level, BlockPos pos) {
        return new RedstoneConduitData();
    }

    @Override
    public boolean canApplyFilter(Void unused, SlotType slotType, ResourceFilter resourceFilter) {
        return switch (slotType) {
            case FILTER_EXTRACT -> resourceFilter instanceof RedstoneExtractFilter;
            case FILTER_INSERT -> resourceFilter instanceof RedstoneInsertFilter;
            default -> false;
        };
    }
}
