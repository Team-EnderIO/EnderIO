package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidConduitTicker
        extends IOAwareConduitTicker<FluidConduit, FluidConduitConnectionConfig, FluidConduitTicker.Connection> {

    public static final FluidConduitTicker INSTANCE = new FluidConduitTicker();

    private int doFluidTransfer(Fluid fluid, int maxTransfer, Connection receiver, List<Connection> senders) {
        // Attempt to drain fluid from the target.
        FluidStack extractedFluid = receiver.fluidHandler().drain(new FluidStack(fluid, maxTransfer), IFluidHandler.FluidAction.SIMULATE);
        if (extractedFluid.isEmpty()) {
            return maxTransfer;
        }

        // Test the extracted fluid against the target
        var extractFilter = receiver.inventory()
                .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EIOCapabilities.FLUID_FILTER);

        if (extractFilter != null) {
            extractedFluid = extractFilter.test(receiver.fluidHandler, extractedFluid);
            if (extractedFluid.isEmpty()) {
                return maxTransfer;
            }
        }

        // Insert into any available blocks
        for (Connection insert : senders) {
            var fluidToInsert = extractedFluid.copy();

            // Test fluid against insert filter.
            var insertFilter = insert.inventory()
                    .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EIOCapabilities.FLUID_FILTER);

            if (insertFilter != null) {
                fluidToInsert = insertFilter.test(insert.fluidHandler, fluidToInsert);
                if (fluidToInsert.isEmpty()) {
                    continue;
                }
            }

            // Attempt to transfer fluid.
            FluidStack transferredFluid = FluidUtil.tryFluidTransfer(insert.fluidHandler(), receiver.fluidHandler(),
                    fluidToInsert, true);

            // Deduct the transferred fluid from our maximum transfer.
            maxTransfer -= transferredFluid.getAmount();
            if (maxTransfer <= 0) {
                break;
            }
        }

        return maxTransfer;
    }

    @Override
    public void tickGraph(ServerLevel level, FluidConduit conduit, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {
        super.tickGraph(level, conduit, graph, coloredRedstoneProvider);

        // Update if the network is now locked
        var context = graph.getOrCreateContext(FluidConduitNetworkContext.TYPE);
        if (!context.lockedFluid().equals(context.lastLockedFluid())) {
            context.clearLastLockedFluid();
            for (var node : graph.getNodes()) {
                if (node.isLoaded()) {
                    node.markDirty();
                }
            }
        }
    }

    @Override
    protected void tickColoredGraph(ServerLevel level, FluidConduit conduit, List<Connection> senders,
            List<Connection> receivers, DyeColor color, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {

        final int fluidRate = conduit.transferRatePerTick() * conduit.graphTickRate();
        var context = graph.getOrCreateContext(FluidConduitNetworkContext.TYPE);

        for (Connection receiver : receivers) {
            IFluidHandler extractHandler = receiver.fluidHandler();

            // Prioritize senders in order of distance.
            var prioritizedSenders = senders.stream()
                    .sorted(Comparator.comparingDouble(e -> e.pos().distSqr(receiver.pos())))
                    .toList();

            if (!context.lockedFluid().isSame(Fluids.EMPTY)) {
                doFluidTransfer(context.lockedFluid(), fluidRate, receiver, prioritizedSenders);
            } else {
                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {
                    if (extractHandler.getFluidInTank(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                    remaining = doFluidTransfer(fluid, remaining, receiver, prioritizedSenders);

                    if (!conduit.isMultiFluid() && remaining < fluidRate) {
                        if (fluid instanceof FlowingFluid flowing) {
                            fluid = flowing.getSource();
                        }

                        context.setLockedFluid(fluid);

                        break;
                    }
                }
            }
        }
    }

    @Override
    protected @Nullable FluidConduitTicker.Connection createConnection(Level level, ConduitNode node, Direction side) {
        IFluidHandler fluidHandler = node.getNeighbourCapability(Capabilities.FluidHandler.BLOCK, side);
        if (fluidHandler != null) {
            return new Connection(node, side, node.getConnectionConfig(side, FluidConduitConnectionConfig.TYPE),
                    fluidHandler);
        }
        return null;
    }

    protected static class Connection extends SimpleConnection<FluidConduitConnectionConfig> {
        private final IFluidHandler fluidHandler;

        public Connection(ConduitNode node, Direction side, FluidConduitConnectionConfig config,
                IFluidHandler fluidHandler) {
            super(node, side, config);
            this.fluidHandler = fluidHandler;
        }

        public IFluidHandler fluidHandler() {
            return fluidHandler;
        }
    }
}
