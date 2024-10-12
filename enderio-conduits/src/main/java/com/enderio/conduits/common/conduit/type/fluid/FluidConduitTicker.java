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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends CapabilityAwareConduitTicker<FluidConduit, IFluidHandler> {

    private int getScaledFluidRate(FluidConduit conduit, CapabilityConnection extractingConnection) {
        // Adjust for tick rate. Always flow up so we are at minimum meeting the required rate.
        int rate = (int)Math.ceil(conduit.transferRatePerTick() * (20.0 / conduit.graphTickRate()));

        // Apply speed upgrade
        if (extractingConnection.upgrade() instanceof ExtractionSpeedUpgrade speedUpgrade) {
            // TODO: Review scaling.
            rate *= (int) Math.pow(2, speedUpgrade.tier());
        }
        return rate;
    }

    private int doFluidTransfer(FluidStack fluid, CapabilityConnection extract, List<CapabilityConnection> inserts) {
        FluidStack extractedFluid = extract.capability().drain(fluid, IFluidHandler.FluidAction.SIMULATE);

        if (extractedFluid.isEmpty()) {
            return fluid.getAmount();
        }

        if (extract.extractFilter() instanceof FluidStackFilter fluidStackFilter) {
            if (!fluidStackFilter.test(extractedFluid)) {
                return fluid.getAmount();
            }
        }

        for (CapabilityConnection insert : inserts) {
            if (insert.insertFilter() instanceof FluidStackFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            FluidStack transferredFluid = FluidUtil.tryFluidTransfer(insert.capability(), extract.capability(), fluid, true);

            if (!transferredFluid.isEmpty()) {
                fluid.shrink(transferredFluid.getAmount());
            }

            if (fluid.getAmount() <= 0) {
                break;
            }
        }

        return fluid.getAmount();
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

            final int fluidRate = getScaledFluidRate(conduit, extract);

            if (!fluidExtendedData.lockedFluid().isSame(Fluids.EMPTY)) {
                doFluidTransfer(new FluidStack(fluidExtendedData.lockedFluid(), fluidRate), extract, inserts);
            } else {
                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {
                    if (extractHandler.getFluidInTank(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                    remaining = doFluidTransfer(new FluidStack(fluid, remaining), extract, inserts);

                    if (!conduit.isMultiFluid() && remaining < fluidRate) {
                        for (ConduitNode node : graph.getNodes()) {
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            node.getOrCreateData(ConduitTypes.Data.FLUID.get()).setLockedFluid(fluid);
                        }

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
