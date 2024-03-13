package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class HeatTicker extends CapabilityAwareConduitTicker<IHeatHandler> {

    public HeatTicker() {
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityAwareConduitTicker<IHeatHandler>.CapabilityConnection> inserts,
        List<CapabilityAwareConduitTicker<IHeatHandler>.CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        for (CapabilityConnection extract : extracts) {
            IHeatHandler extractHandler = extract.cap;

            for (CapabilityConnection insert : inserts) {
                IHeatHandler insertHandler = insert.cap;

                double heatCapacity = extractHandler.getTotalHeatCapacity();
                double invConduction = insertHandler.getTotalInverseConduction() + extractHandler.getTotalInverseConduction();
                double tempToTransfer = (extractHandler.getTotalTemperature() - insertHandler.getTotalTemperature()) / invConduction; //TODO subtract ambient? - HeatAPI.getAmbientTemp(level, )

                double heatToTransfer = tempToTransfer * heatCapacity;
                if (heatToTransfer > 0) {
                    extractHandler.handleHeat(-heatToTransfer);
                    insertHandler.handleHeat(heatToTransfer);
                }

            }
        }
    }

    @Override
    protected BlockCapability<IHeatHandler, Direction> getCapability() {
        return MekanismIntegration.HEAT;
    }
}
