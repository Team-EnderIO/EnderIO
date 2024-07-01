//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.api.conduit.ColoredRedstoneProvider;
//import com.enderio.api.conduit.ConduitData;
//import com.enderio.api.conduit.ConduitNetwork;
//import com.enderio.api.conduit.ConduitNetworkContext;
//import com.enderio.api.conduit.ConduitType;
//import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
//import mekanism.api.heat.IHeatHandler;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//
//import java.util.List;
//
//public class HeatTicker extends CapabilityAwareConduitTicker<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData, IHeatHandler> {
//
//    public HeatTicker() {
//    }
//
//    @Override
//    protected void tickCapabilityGraph(
//        ServerLevel level,
//        ConduitType<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData> type,
//        List<CapabilityAwareConduitTicker<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData, IHeatHandler>.CapabilityConnection> inserts,
//        List<CapabilityAwareConduitTicker<Void, ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData, IHeatHandler>.CapabilityConnection> extracts,
//        ConduitNetwork<ConduitNetworkContext.Dummy, ConduitData.EmptyConduitData> graph, ColoredRedstoneProvider coloredRedstoneProvider) {
//
//        for (var extract : extracts) {
//            IHeatHandler extractHandler = extract.capability;
//
//            for (var insert : inserts) {
//                IHeatHandler insertHandler = insert.capability;
//
//                double heatCapacity = extractHandler.getTotalHeatCapacity();
//                double invConduction = insertHandler.getTotalInverseConduction() + extractHandler.getTotalInverseConduction();
//                double tempToTransfer = (extractHandler.getTotalTemperature() - insertHandler.getTotalTemperature()) / invConduction; //TODO subtract ambient? - HeatAPI.getAmbientTemp(level, )
//
//                double heatToTransfer = tempToTransfer * heatCapacity;
//                if (heatToTransfer > 0) {
//                    extractHandler.handleHeat(-heatToTransfer);
//                    insertHandler.handleHeat(heatToTransfer);
//                }
//
//            }
//        }
//    }
//
//    @Override
//    protected BlockCapability<IHeatHandler, Direction> getCapability() {
//        return MekanismIntegration.HEAT;
//    }
//}
