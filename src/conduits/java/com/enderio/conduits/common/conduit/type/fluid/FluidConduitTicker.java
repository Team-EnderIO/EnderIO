package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.filter.FluidStackFilter;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.conduits.common.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.function.TriFunction;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends CapabilityAwareConduitTicker<FluidExtendedData, IFluidHandler> {

    private final boolean lockFluids;
    private final int fluidRate;

    public FluidConduitTicker(boolean lockFluids, int fluidRate) {
        this.lockFluids = lockFluids;
        this.fluidRate = fluidRate;
    }

    @Override
    public void tickGraph(
        ConduitType<FluidExtendedData> type,
        List<ConduitNode<FluidExtendedData>> loadedNodes,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        boolean shouldReset = false;
        for (ConduitNode<FluidExtendedData> loadedNode : loadedNodes) {
            FluidExtendedData fluidExtendedData = loadedNode.getExtendedConduitData();
            if (fluidExtendedData.shouldReset) {
                shouldReset = true;
                fluidExtendedData.shouldReset = false;
            }
        }
        if (shouldReset) {
            for (ConduitNode<?> loadedNode : loadedNodes) {
                loadedNode.getExtendedConduitData().castTo(FluidExtendedData.class).lockedFluid = null;
            }
        }
        super.tickGraph(type, loadedNodes, level, graph, coloredRedstoneProvider);
    }

    @Override
    protected void tickCapabilityGraph(
        ConduitType<FluidExtendedData> type,
        List<CapabilityConnection<FluidExtendedData, IFluidHandler>> inserts,
        List<CapabilityConnection<FluidExtendedData, IFluidHandler>> extracts,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (CapabilityConnection<FluidExtendedData, IFluidHandler> extract : extracts) {
            IFluidHandler extractHandler = extract.capability();
            FluidExtendedData fluidExtendedData = extract.data();

            int temp = fluidRate;
            if (extract.upgrade() instanceof FluidSpeedUpgrade speedUpgrade) {
                temp *= speedUpgrade.getSpeed();
            }

            final int rate = temp;

            FluidStack extractedFluid = Optional
                .ofNullable(fluidExtendedData.lockedFluid)
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, rate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(rate, IFluidHandler.FluidAction.SIMULATE));

            if (extractedFluid.isEmpty()) {
                continue;
            }

            if (extract.extractFilter() instanceof FluidStackFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            int transferred = 0;
            for (CapabilityConnection<FluidExtendedData, IFluidHandler> insert : inserts) {
                if (extract.insertFilter() instanceof FluidStackFilter fluidStackFilter) {
                    if (!fluidStackFilter.test(extractedFluid)) {
                        continue;
                    }
                }

                FluidStack transferredFluid = fluidExtendedData.lockedFluid != null ?
                    FluidUtil.tryFluidTransfer(insert.capability(), extractHandler, new FluidStack(fluidExtendedData.lockedFluid, fluidRate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(insert.capability(), extractHandler, rate - transferred, true);

                if (!transferredFluid.isEmpty()) {
                    transferred += transferredFluid.getAmount();
                    if (lockFluids) {
                        for (GraphObject<Mergeable.Dummy> graphObject : graph.getObjects()) {
                            if (graphObject instanceof NodeIdentifier<?> node) {
                                Fluid fluid = transferredFluid.getFluid();
                                if (fluid instanceof FlowingFluid flowing) {
                                    fluid = flowing.getSource();
                                }

                                node.getExtendedConduitData().castTo(FluidExtendedData.class).lockedFluid = fluid;
                            }
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
