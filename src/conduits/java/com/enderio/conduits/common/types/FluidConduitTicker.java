package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.ICapabilityAwareConduitTicker;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends ICapabilityAwareConduitTicker<IFluidHandler> {

    private final boolean lockFluids;
    private final int fluidRate;

    public FluidConduitTicker(boolean lockFluids, int fluidRate) {
        this.lockFluids = lockFluids;
        this.fluidRate = fluidRate;
    }

    @Override
    public void tickGraph(IConduitType<?> type, List<NodeIdentifier<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph) {
        boolean shouldReset = false;
        for (NodeIdentifier<?> loadedNode : loadedNodes) {
            FluidExtendedData fluidExtendedData = loadedNode.getExtendedConduitData().castTo(FluidExtendedData.class);
            if (fluidExtendedData.shouldReset) {
                shouldReset = true;
                fluidExtendedData.shouldReset = false;
            }
        }
        if (shouldReset) {
            for (NodeIdentifier<?> loadedNode : loadedNodes) {
                loadedNode.getExtendedConduitData().castTo(FluidExtendedData.class).lockedFluid = null;
            }
        }
        super.tickGraph(type, loadedNodes, level, graph);
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<ICapabilityAwareConduitTicker<IFluidHandler>.CapabilityConnection> inserts,
        List<ICapabilityAwareConduitTicker<IFluidHandler>.CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph) {

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.cap;
            FluidExtendedData fluidExtendedData = extract.data.castTo(FluidExtendedData.class);
            FluidStack extractedFluid = Optional
                .ofNullable(fluidExtendedData.lockedFluid)
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, fluidRate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(fluidRate, IFluidHandler.FluidAction.SIMULATE));
            if (extractedFluid.isEmpty())
                continue;
            int transferred = 0;
            for (int j = 0; j < inserts.size(); j++) {
                FluidStack transferredFluid = fluidExtendedData.lockedFluid != null ?
                    FluidUtil.tryFluidTransfer(inserts.get(j).cap, extractHandler, new FluidStack(fluidExtendedData.lockedFluid, fluidRate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(inserts.get(j).cap, extractHandler, fluidRate - transferred, true);

                if (!transferredFluid.isEmpty()) {
                    transferred += transferredFluid.getAmount();
                    if (lockFluids) {
                        for (GraphObject<Mergeable.Dummy> graphObject : graph.getObjects()) {
                            if (graphObject instanceof NodeIdentifier<?> node) {
                                node.getExtendedConduitData().castTo(FluidExtendedData.class).lockedFluid = transferredFluid.getFluid();
                            }
                        }
                    }
                    if (transferred > fluidRate)
                        break;
                }
            }
        }
    }

    @Override
    protected Capability<IFluidHandler> getCapability() {
        return ForgeCapabilities.FLUID_HANDLER;
    }
}
