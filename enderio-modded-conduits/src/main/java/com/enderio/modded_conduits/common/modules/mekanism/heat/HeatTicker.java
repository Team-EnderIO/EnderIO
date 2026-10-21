package com.enderio.modded_conduits.common.modules.mekanism.heat;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class HeatTicker extends ConduitTickerBase<HeatConduit> {

    public static final HeatTicker INSTANCE = new HeatTicker();

    private HeatTicker() {
        super(MekanismModule.TYPE_HEAT::get);
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits) {
        for (var extractConnection : network.extractConnections()) {
            var insertPaths = network.insertConnectionsFrom(extractConnection);
            if (insertPaths.isEmpty()) {
                continue;
            }

            IHeatHandler extractHandler = extractConnection.getSidedCapability(MekanismModule.Capabilities.HEAT);
            if (extractHandler == null) {
                continue;
            }

            for (var insertPath : insertPaths) {
                var insertConnection = insertPath.end();
                IHeatHandler insertHandler = insertConnection.getSidedCapability(MekanismModule.Capabilities.HEAT);
                if (insertHandler == null || insertHandler == extractHandler) {
                    continue;
                }

                double heatCapacity = extractHandler.getHeatCapacity();
                double invConduction = insertHandler.getInverseConduction()
                    + extractHandler.getInverseConduction();
                double tempToTransfer = (extractHandler.getTemperature() - insertHandler.getTemperature())
                    / invConduction; // TODO subtract ambient? - HeatAPI.getAmbientTemp(level, )

                double heatToTransfer = tempToTransfer * heatCapacity;
                if (heatToTransfer > 0) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        extractHandler.handleHeat(-heatToTransfer, transaction);
                        insertHandler.handleHeat(heatToTransfer, transaction);
                        transaction.commit();
                    }
                }
            }
        }
    }
}
