package com.enderio.modded_conduits.common.modules.mekanism.heat;

import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.server.level.ServerLevel;

public class HeatTicker extends ConduitTickerBase<HeatConduit> {

    public static final HeatTicker INSTANCE = new HeatTicker();

    private HeatTicker() {
    }

    @Override
    protected ConduitType<HeatConduit> conduitType() {
        return MekanismModule.TYPE_HEAT.get();
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, int tickOffset) {
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
