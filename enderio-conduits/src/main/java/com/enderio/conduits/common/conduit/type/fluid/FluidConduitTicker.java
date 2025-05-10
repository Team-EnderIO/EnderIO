package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.network.ConduitBlockConnection;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidConduitTicker implements ConduitTicker<FluidConduit> {

    public static final FluidConduitTicker INSTANCE = new FluidConduitTicker();

    @Override
    public void tick(ServerLevel level, FluidConduit conduit, IConduitNetwork network) {
        final int fluidRate = conduit.transferRatePerTick() * conduit.networkTickRate();
        var context = network.getOrCreateContext(FluidConduitNetworkContext.TYPE);

        for (var channel : network.allChannels()) {
            for (var receivingConnection : network.receivingConnections(channel)) {
                IFluidHandler extractHandler = receivingConnection.getSidedCapability(Capabilities.FluidHandler.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                var senders = network.sendingConnectionsFrom(receivingConnection);

                if (!context.lockedFluid().isSame(Fluids.EMPTY)) {
                    doFluidTransfer(context.lockedFluid(), fluidRate, receivingConnection, senders);
                } else {
                    int remaining = fluidRate;

                    for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {
                        if (extractHandler.getFluidInTank(i).isEmpty()) {
                            continue;
                        }

                        Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                        remaining = doFluidTransfer(fluid, remaining, receivingConnection, senders);

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

        // Mark nodes as dirty if we've acquired a new locked fluid
        if (!conduit.isMultiFluid()) {
            if (context != null && !context.lockedFluid().equals(context.lastLockedFluid())) {
                context.clearLastLockedFluid();
                for (var node : network.loadedNodes()) {
                    node.markDirty();
                }
            }
        }
    }

    private int doFluidTransfer(Fluid fluid, int maxTransfer, ConduitBlockConnection receiver,
            List<ConduitBlockConnection> senders) {
        var receiverHandler = Objects.requireNonNull(receiver.getSidedCapability(Capabilities.FluidHandler.BLOCK));

        // Attempt to drain fluid from the target.
        FluidStack extractedFluid = receiverHandler.drain(new FluidStack(fluid, maxTransfer),
                IFluidHandler.FluidAction.SIMULATE);
        if (extractedFluid.isEmpty()) {
            return maxTransfer;
        }

        // Test the extracted fluid against the target
        var extractFilter = receiver.inventory()
                .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EIOCapabilities.FLUID_FILTER);

        if (extractFilter != null) {
            extractedFluid = extractFilter.test(receiverHandler, extractedFluid);
            if (extractedFluid.isEmpty()) {
                return maxTransfer;
            }
        }

        // Insert into any available blocks
        for (var insert : senders) {
            IFluidHandler insertHandler = insert.getSidedCapability(Capabilities.FluidHandler.BLOCK);
            if (insertHandler == null) {
                continue;
            }

            var fluidToInsert = extractedFluid.copy();

            // Test fluid against insert filter.
            var insertFilter = insert.inventory()
                    .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EIOCapabilities.FLUID_FILTER);

            if (insertFilter != null) {
                fluidToInsert = insertFilter.test(insertHandler, fluidToInsert);
                if (fluidToInsert.isEmpty()) {
                    continue;
                }
            }

            // Attempt to transfer fluid.
            FluidStack transferredFluid = FluidUtil.tryFluidTransfer(insertHandler, receiverHandler, fluidToInsert,
                    true);

            // Deduct the transferred fluid from our maximum transfer.
            maxTransfer -= transferredFluid.getAmount();
            if (maxTransfer <= 0) {
                break;
            }
        }

        return maxTransfer;
    }
}
