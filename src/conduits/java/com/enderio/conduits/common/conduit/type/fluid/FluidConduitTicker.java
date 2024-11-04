package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.conduits.common.capability.ExtractionSpeedUpgrade;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends CapabilityAwareConduitTicker<FluidConduitData, IFluidHandler> {

    private final boolean lockFluids;
    private final int baseFluidRate;

    public FluidConduitTicker(boolean lockFluids, int baseFluidRate) {
        this.lockFluids = lockFluids;
        this.baseFluidRate = baseFluidRate;
    }

    private int getScaledFluidRate(CapabilityConnection extractingConnection) {
        // Adjust for tick rate. Always flow up so we are at minimum meeting the required rate.
        int rate = (int)Math.ceil(baseFluidRate * (20.0 / getTickRate()));

        if (extractingConnection.upgrade instanceof ExtractionSpeedUpgrade speedUpgrade) {
            // TODO: Review scaling.
            rate *= (int) Math.pow(2, speedUpgrade.tier());
        }
        return rate;
    }

    private int doFluidTransfer(FluidStack fluid, CapabilityConnection extract, List<CapabilityConnection> inserts) {
        FluidStack extractedFluid = extract.capability.drain(fluid, IFluidHandler.FluidAction.SIMULATE);

        if (extractedFluid.isEmpty()) {
            return fluid.getAmount();
        }

        if (extract.extractFilter instanceof FluidStackFilter fluidStackFilter) {
            if (!fluidStackFilter.test(extractedFluid)) {
                return fluid.getAmount();
            }
        }

        for (CapabilityConnection insert : inserts) {
            if (insert.insertFilter instanceof FluidStackFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            FluidStack transferredFluid = FluidUtil.tryFluidTransfer(insert.capability, extract.capability, fluid, true);

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
        ConduitType<FluidConduitData> type,
        List<ConduitNode<FluidConduitData>> loadedNodes,
        ConduitGraph<FluidConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        boolean shouldReset = false;
        for (var loadedNode : loadedNodes) {
            FluidConduitData fluidConduitData = loadedNode.getConduitData().castTo(FluidConduitData.class);
            if (fluidConduitData.shouldReset()) {
                shouldReset = true;
                fluidConduitData.setShouldReset(false);
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
        ConduitType<FluidConduitData> type,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitGraph<FluidConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.capability;
            FluidConduitData fluidConduitData = extract.data.castTo(FluidConduitData.class);

            final int fluidRate = getScaledFluidRate(extract);

            if (fluidConduitData.lockedFluid() != null) {
                doFluidTransfer(new FluidStack(fluidConduitData.lockedFluid(), fluidRate), extract, inserts);
            } else {
                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {

                    if (extractHandler.getFluidInTank(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                    remaining = doFluidTransfer(new FluidStack(fluid, remaining), extract, inserts);

                    if (lockFluids && remaining < fluidRate) {
                        for (ConduitNode<FluidConduitData> node : graph.getNodes()) {
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            node.getConduitData().setLockedFluid(fluid);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected Capability<IFluidHandler> getCapability() {
        return ForgeCapabilities.FLUID_HANDLER;
    }
}
