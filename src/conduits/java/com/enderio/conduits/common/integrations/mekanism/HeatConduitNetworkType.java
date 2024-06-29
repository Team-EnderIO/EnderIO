package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SimpleConduitNetworkType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class HeatConduitNetworkType implements SimpleConduitNetworkType<ConduitData.EmptyConduitData> {

    private static final HeatTicker TICKER = new HeatTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public HeatTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData(Void unused) {
        return MENU_DATA;
    }

    @Override
    public ConduitData.EmptyConduitData createConduitData(ConduitType<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData> type, Level level,
        BlockPos pos) {
        return ConduitData.EMPTY;
    }
}
