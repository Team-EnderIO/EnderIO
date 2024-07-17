package com.enderio.modconduits.mods.mekanism;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HeatConduit(
    ResourceLocation texture,
    Component description
) implements Conduit<HeatConduit> {

    private static final HeatTicker TICKER = new HeatTicker();
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    @Override
    public ConduitType<HeatConduit> type() {
        return MekanismModule.Types.HEAT.get();
    }

    @Override
    public HeatTicker getTicker() {
        return TICKER;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public int compareTo(@NotNull HeatConduit o) {
        return 0;
    }
}
