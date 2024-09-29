package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.base.api.filter.FluidStackFilter;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitTypes;
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

public class FluidConduitTicker extends CapabilityAwareConduitTicker<FluidConduit, IFluidHandler> {

    private int getScaledFluidRate(FluidConduit conduit, CapabilityConnection extractingConnection) {
        int rate = conduit.transferRate();
        if (extractingConnection.upgrade() instanceof ExtractionSpeedUpgrade speedUpgrade) {
            // TODO: Review scaling.
            rate *= (int) Math.pow(2, speedUpgrade.tier());
        }
        return rate;
    }

    @Override
    public void tickGraph(
        ServerLevel level,
        FluidConduit conduit,
        List<ConduitNode> loadedNodes,
        ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        boolean shouldReset = false;
        for (var loadedNode : loadedNodes) {
            FluidConduitData fluidExtendedData = loadedNode.getOrCreateData(ConduitTypes.Data.FLUID.get());
            if (fluidExtendedData.shouldReset()) {
                shouldReset = true;
                fluidExtendedData.setShouldReset(false);
            }
        }

        if (shouldReset) {
            for (var loadedNode : loadedNodes) {
                FluidConduitData fluidExtendedData = loadedNode.getOrCreateData(ConduitTypes.Data.FLUID.get());
                fluidExtendedData.setLockedFluid(null);
            }
        }
        super.tickGraph(level, conduit, loadedNodes, graph, coloredRedstoneProvider);
    }

    @Override
    protected void tickCapabilityGraph(
        ServerLevel level,
        FluidConduit conduit,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.capability();
            FluidConduitData fluidExtendedData = extract.node().getOrCreateData(ConduitTypes.Data.FLUID.get());

            final int transferRate = getScaledFluidRate(conduit, extract);

            FluidStack extractedFluid = Optional
                .ofNullable(fluidExtendedData.lockedFluid())
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, transferRate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(transferRate, IFluidHandler.FluidAction.SIMULATE));

            if (extractedFluid.isEmpty()) {
                continue;
            }

            if (extract.extractFilter() instanceof FluidStackFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            int transferred = 0;
            for (CapabilityConnection insert : inserts) {
                if (insert.insertFilter() instanceof FluidStackFilter fluidStackFilter) {
                    if (!fluidStackFilter.test(extractedFluid)) {
                        continue;
                    }
                }

                FluidStack transferredFluid = fluidExtendedData.lockedFluid() != null ?
                    FluidUtil.tryFluidTransfer(insert.capability(), extractHandler, new FluidStack(fluidExtendedData.lockedFluid(), transferRate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(insert.capability(), extractHandler, transferRate - transferred, true);

                if (!transferredFluid.isEmpty()) {
                    transferred += transferredFluid.getAmount();
                    if (!conduit.isMultiFluid()) {
                        for (ConduitNode node : graph.getNodes()) {
                            Fluid fluid = transferredFluid.getFluid();
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            FluidConduitData nodeFluidData = node.getOrCreateData(ConduitTypes.Data.FLUID.get());
                            nodeFluidData.setLockedFluid(fluid);
                        }
                    }

                    if (transferred > transferRate) {
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
