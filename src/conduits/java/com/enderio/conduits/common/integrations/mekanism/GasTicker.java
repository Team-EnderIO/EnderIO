package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class GasTicker extends CapabilityAwareConduitTicker<IGasHandler> {

    private final int gasRate;

    public GasTicker(int gasRate) {
        this.gasRate = gasRate;
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityAwareConduitTicker<IGasHandler>.CapabilityConnection> inserts,
        List<CapabilityAwareConduitTicker<IGasHandler>.CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        for (CapabilityConnection extract : extracts) {
            IGasHandler extractHandler = extract.cap;
            GasExtendedData gasExtendedData = extract.data.castTo(GasExtendedData.class);
            GasStack extractedGas = Optional
                .ofNullable(gasExtendedData.lockedGas)
                .map(gas -> extractHandler.extractChemical(new GasStack(gas, gasRate), Action.SIMULATE))
                .orElseGet(() -> extractHandler.extractChemical(gasRate, Action.SIMULATE));

            if (extractedGas.isEmpty()) {
                continue;
            }

            int transferred = 0;
//            for (CapabilityAwareConduitTicker<IGasHandler>.CapabilityConnection insert : inserts) {
//                GasStack transferredGas = gasExtendedData.lockedGas != null ?
//                    FluidUtil.tryFluidTransfer(insert.cap, extractHandler, new GasStack(gasExtendedData.lockedGas, gasRate - transferred),
//                        true) :
//                    FluidUtil.tryFluidTransfer(insert.cap, extractHandler, gasRate - transferred, true);
//
//                if (!transferredGas.isEmpty()) {
//                    transferred += transferredGas.getAmount();
//
//                    if (transferred > gasRate) {
//                        break;
//                    }
//                }
//            }
        }
    }

    @Override
    protected BlockCapability<IGasHandler, Direction> getCapability() {
        return Capabilities.GAS.block();
    }
}
