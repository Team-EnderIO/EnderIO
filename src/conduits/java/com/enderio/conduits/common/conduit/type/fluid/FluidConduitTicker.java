package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.api.capability.IConduitUpgrade;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.conduits.common.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.components.FluidSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitCapabilities;
import com.enderio.core.common.capability.FluidFilterCapability;
import com.enderio.core.common.capability.IFilterCapability;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class FluidConduitTicker extends CapabilityAwareConduitTicker<IFluidHandler> {

    private final boolean lockFluids;
    private final int fluidRate;

    public FluidConduitTicker(boolean lockFluids, int fluidRate) {
        this.lockFluids = lockFluids;
        this.fluidRate = fluidRate;
    }

    @Override
    public void tickGraph(ConduitType<?> type, List<ConduitNode<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        boolean shouldReset = false;
        for (ConduitNode<?> loadedNode : loadedNodes) {
            FluidExtendedData fluidExtendedData = loadedNode.getExtendedConduitData().castTo(FluidExtendedData.class);
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
        super.tickGraph(type, loadedNodes, level, graph, isRedstoneActive);
    }

    @Override
    protected void tickCapabilityGraph(ConduitType<?> type, List<CapabilityAwareConduitTicker<IFluidHandler>.CapabilityConnection> inserts,
                                       List<CapabilityAwareConduitTicker<IFluidHandler>.CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.cap;
            FluidExtendedData fluidExtendedData = extract.data.castTo(FluidExtendedData.class);
            int temp = fluidRate;
            if (extract.connectionState != null) {
                ItemStack upgradeStack = extract.connectionState.upgradeExtract();
                IConduitUpgrade upgrade = upgradeStack.getCapability(ConduitCapabilities.ConduitUpgrade.ITEM);
                if (upgrade instanceof FluidSpeedUpgrade speedUpgrade) {
                    temp *= speedUpgrade.getSpeed();
                }
            }
            final int rate = temp;

            FluidStack extractedFluid = Optional
                .ofNullable(fluidExtendedData.lockedFluid)
                .map(fluid -> extractHandler.drain(new FluidStack(fluid, rate), IFluidHandler.FluidAction.SIMULATE))
                .orElseGet(() -> extractHandler.drain(rate, IFluidHandler.FluidAction.SIMULATE));

            if (extractedFluid.isEmpty()) {
                continue;
            }

            if (extract.connectionState != null && !extract.connectionState.filterExtract().isEmpty()) {
                ItemStack stack = extract.connectionState.filterExtract();
                IFilterCapability capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
                if (capability instanceof FluidFilterCapability cap && !cap.test(extractedFluid)) {
                    continue;
                }
            }

            int transferred = 0;
            for (CapabilityAwareConduitTicker<IFluidHandler>.CapabilityConnection insert : inserts) {

                if (insert.connectionState != null && !insert.connectionState.filterInsert().isEmpty()) {
                    ItemStack stack = insert.connectionState.filterInsert();
                    IFilterCapability capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
                    if (capability instanceof FluidFilterCapability cap && !cap.test(extractedFluid)) {
                        continue;
                    }
                }

                FluidStack transferredFluid = fluidExtendedData.lockedFluid != null ?
                    FluidUtil.tryFluidTransfer(insert.cap, extractHandler, new FluidStack(fluidExtendedData.lockedFluid, fluidRate - transferred),
                        true) :
                    FluidUtil.tryFluidTransfer(insert.cap, extractHandler, rate - transferred, true);

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
