//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.api.conduit.ConduitData;
//import com.enderio.api.conduit.ConduitMenuData;
//import com.enderio.api.conduit.ticker.ConduitTicker;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//
//public class HeatConduitType extends ConduitType<ConduitData.EmptyConduitData> {
//
//    public static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
//
//    @Override
//    public ConduitTicker<ConduitData.EmptyConduitData> getTicker() {
//        return new HeatTicker();
//    }
//
//    @Override
//    public ConduitMenuData getMenuData() {
//        return MENU_DATA;
//    }
//
//    @Override
//    public ConduitData.EmptyConduitData createConduitData(Level level, BlockPos pos) {
//        return ConduitData.EmptyConduitData.EMPTY;
//    }
//}
