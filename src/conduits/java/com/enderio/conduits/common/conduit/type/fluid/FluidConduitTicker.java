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
    private final int fluidRate;

    public FluidConduitTicker(boolean lockFluids, int fluidRate) {
        this.lockFluids = lockFluids;
        this.fluidRate = fluidRate;
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

            int temp = fluidRate;
            if (extract.upgrade instanceof ExtractionSpeedUpgrade speedUpgrade) {
                // TODO: Review scaling.
                temp *= (int) Math.pow(2, speedUpgrade.tier());
            }

            final int rate = temp;

            FluidStack extractedFluid = Optional
                .ofNullable(fluidConduitData.lockedFluid())
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, rate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(fluidRate, IFluidHandler.FluidAction.SIMULATE));

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

                FluidStack transferredFluid = fluidConduitData.lockedFluid() != null ?
                    FluidUtil.tryFluidTransfer(insert.capability, extractHandler, new FluidStack(fluidConduitData.lockedFluid(), fluidRate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(insert.capability, extractHandler, fluidRate - transferred, true);

                if (!transferredFluid.isEmpty()) {
                    transferred += transferredFluid.getAmount();
                    if (lockFluids) {
                        for (ConduitNode<FluidConduitData> node : graph.getNodes()) {
                            Fluid fluid = transferredFluid.getFluid();
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            node.getConduitData().setLockedFluid(fluid);
                        }
                    }

                    if (transferred > fluidRate) {
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
