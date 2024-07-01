package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends CapabilityAwareConduitTicker<FluidConduitType, ConduitNetworkContext.Dummy, FluidConduitData, IFluidHandler> {

    @Override
    public void tickGraph(
        ServerLevel level,
        FluidConduitType type,
        List<ConduitNode<ConduitNetworkContext.Dummy, FluidConduitData>> loadedNodes,
        ConduitNetwork<ConduitNetworkContext.Dummy, FluidConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        boolean shouldReset = false;
        for (var loadedNode : loadedNodes) {
            FluidConduitData fluidExtendedData = loadedNode.getConduitData();
            if (fluidExtendedData.shouldReset()) {
                shouldReset = true;
                fluidExtendedData.setShouldReset(false);
            }
        }

        if (shouldReset) {
            for (var loadedNode : loadedNodes) {
                loadedNode.getConduitData().setLockedFluid(null);
            }
        }
        super.tickGraph(level, type, loadedNodes, graph, coloredRedstoneProvider);
    }

    @Override
    protected void tickCapabilityGraph(
        ServerLevel level,
        FluidConduitType type,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitNetwork<ConduitNetworkContext.Dummy, FluidConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.capability;
            FluidConduitData fluidExtendedData = extract.data;

            int temp = type.transferRate();
            if (extract.upgrade instanceof ExtractionSpeedUpgrade speedUpgrade) {
                // TODO: Review scaling.
                temp *= (int) Math.pow(2, speedUpgrade.tier());
            }

            final int rate = temp;

            FluidStack extractedFluid = Optional
                .ofNullable(fluidExtendedData.lockedFluid())
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, rate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(rate, IFluidHandler.FluidAction.SIMULATE));

            if (extractedFluid.isEmpty()) {
                continue;
            }

            if (extract.extractFilter instanceof FluidStackFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            int transferred = 0;
            for (CapabilityConnection insert : inserts) {
                if (insert.insertFilter instanceof FluidStackFilter fluidStackFilter) {
                    if (!fluidStackFilter.test(extractedFluid)) {
                        continue;
                    }
                }

                FluidStack transferredFluid = fluidExtendedData.lockedFluid() != null ?
                    FluidUtil.tryFluidTransfer(insert.capability, extractHandler, new FluidStack(fluidExtendedData.lockedFluid(), rate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(insert.capability, extractHandler, rate - transferred, true);

                if (!transferredFluid.isEmpty()) {
                    transferred += transferredFluid.getAmount();
                    if (!type.isMultiFluid()) {
                        for (ConduitNode<ConduitNetworkContext.Dummy, FluidConduitData> node : graph.getNodes()) {
                            Fluid fluid = transferredFluid.getFluid();
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            node.getConduitData().setLockedFluid(fluid);
                        }
                    }

                    if (transferred > rate) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected BlockCapability<IFluidHandler, Direction> getCapability() {
        return Capabilities.FluidHandler.BLOCK;
    }
}
