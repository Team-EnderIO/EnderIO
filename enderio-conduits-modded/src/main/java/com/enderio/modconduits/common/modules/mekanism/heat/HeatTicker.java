package com.enderio.modconduits.common.modules.mekanism.heat;

import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.server.level.ServerLevel;

public class HeatTicker implements ConduitTicker<HeatConduit> {

    public HeatTicker() {
    }

    @Override
    public void tick(ServerLevel level, HeatConduit conduit, IConduitNetwork network) {
        for (var extractConnection : network.extractConnections()) {
            var insertConnections = network.insertConnectionsFrom(extractConnection);
            if (insertConnections.isEmpty()) {
                continue;
            }

            IHeatHandler extractHandler = extractConnection.getSidedCapability(MekanismModule.Capabilities.HEAT);
            if (extractHandler == null) {
                continue;
            }

            for (var insertConnection : insertConnections) {
                IHeatHandler insertHandler = insertConnection.getSidedCapability(MekanismModule.Capabilities.HEAT);
                if (insertHandler == null) {
                    continue;
                }

                double heatCapacity = extractHandler.getTotalHeatCapacity();
                double invConduction = insertHandler.getTotalInverseConduction()
                        + extractHandler.getTotalInverseConduction();
                double tempToTransfer = (extractHandler.getTotalTemperature() - insertHandler.getTotalTemperature())
                        / invConduction; // TODO subtract ambient? - HeatAPI.getAmbientTemp(level, )

                double heatToTransfer = tempToTransfer * heatCapacity;
                if (heatToTransfer > 0) {
                    extractHandler.handleHeat(-heatToTransfer);
                    insertHandler.handleHeat(heatToTransfer);
                }
            }
        }
    }
}
