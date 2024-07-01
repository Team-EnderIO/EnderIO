//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.api.conduit.ConduitData;
//import com.enderio.api.conduit.ConduitMenuData;
//import com.enderio.api.conduit.ConduitNetworkContext;
//import com.enderio.api.conduit.ConduitType;
//import com.enderio.api.conduit.ConduitTypeSerializer;
//import com.enderio.api.conduit.SimpleConduitNetworkType;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.NotNull;
//
//public record HeatConduitNetworkType(
//    ResourceLocation texture,
//    Component description
//) implements SimpleConduitNetworkType<HeatConduitNetworkType, ConduitData.EmptyConduitData> {
//
//    private static final HeatTicker TICKER = new HeatTicker();
//    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);
//
//    // TODO: Plug in Mek conduits
//    @Override
//    public ConduitTypeSerializer<HeatConduitNetworkType> serializer() {
//        return null;
//    }
//
//    @Override
//    public HeatTicker getTicker() {
//        return TICKER;
//    }
//
//    @Override
//    public ConduitMenuData getMenuData() {
//        return MENU_DATA;
//    }
//
//    @Override
//    public ConduitData.EmptyConduitData createConduitData(Level level, BlockPos pos) {
//        return ConduitData.EMPTY;
//    }
//
//    @Override
//    public int compareTo(@NotNull HeatConduitNetworkType o) {
//        return 0;
//    }
//}
