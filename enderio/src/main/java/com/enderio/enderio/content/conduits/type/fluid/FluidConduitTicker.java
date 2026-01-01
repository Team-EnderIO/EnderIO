package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.network.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTicker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Objects;

public class FluidConduitTicker implements ConduitTicker<FluidConduit> {

    public static final FluidConduitTicker INSTANCE = new FluidConduitTicker();

    @Override
    public void tick(ServerLevel level, FluidConduit conduit, ConduitNetwork network) {
        final int fluidRate = conduit.transferRatePerTick() * conduit.networkTickRate();
        var context = network.getOrCreateContext(FluidConduitNetworkContext.TYPE);

        for (var channel : network.allChannels()) {
            for (var extractConnection : network.extractConnections(channel)) {
                var insertConnections = network.insertConnectionsFrom(extractConnection);
                if (insertConnections.isEmpty()) {
                    continue;
                }

                ResourceHandler<FluidResource> extractHandler = extractConnection.getSidedCapability(Capabilities.Fluid.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                if (!context.lockedFluid().isSame(Fluids.EMPTY)) {
                    doFluidTransfer(context.lockedFluid(), fluidRate, extractConnection, insertConnections);
                } else {
                    int remaining = fluidRate;

                    for (int i = 0; i < extractHandler.size() && remaining > 0; i++) {
                        if (extractHandler.getResource(i).isEmpty()) {
                            continue;
                        }

                        Fluid fluid = extractHandler.getResource(i).getFluid();
                        remaining = doFluidTransfer(fluid, remaining, extractConnection, insertConnections);

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
                for (var node : network.tickingNodes()) {
                    node.markDirty();
                }
            }
        }
    }

    private int doFluidTransfer(Fluid fluid, int maxTransfer, ConduitBlockConnection extractConnection,
            List<ConduitBlockConnection> insertConnections) {

        var fluidResource = FluidResource.of(fluid);

        var extractHandler = Objects
                .requireNonNull(extractConnection.getSidedCapability(Capabilities.Fluid.BLOCK));

        try (Transaction transaction = Transaction.openRoot()) {
            int maxExtract;
            try (Transaction nestedTransaction = Transaction.open(transaction)) {
                maxExtract = extractHandler.extract(fluidResource, maxTransfer, nestedTransaction);
            }

            if (maxExtract <= 0) {
                return maxTransfer;
            }

            // Test the extracted fluid against the target
            var extractFilter = extractConnection.inventory()
                .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EnderIOCapabilities.FLUID_FILTER);

            if (extractFilter != null) {
                var filteredStack = extractFilter.test(IFluidHandler.of(extractHandler), fluidResource.toStack(maxExtract));
                if (filteredStack.isEmpty()) {
                    return maxTransfer;
                }

                maxExtract = filteredStack.getAmount();
            }

            // Insert into any available blocks
            for (var insertConnection : insertConnections) {
                var insertHandler = insertConnection.getSidedCapability(Capabilities.Fluid.BLOCK);
                if (insertHandler == null) {
                    continue;
                }

                int amountToInsert = maxExtract;

                // Test fluid against insert filter.
                var insertFilter = insertConnection.inventory()
                    .getStackInSlot(FluidConduit.INSERT_FILTER_SLOT)
                    .getCapability(EnderIOCapabilities.FLUID_FILTER);

                if (insertFilter != null) {
                    var filteredStack = insertFilter.test(IFluidHandler.of(insertHandler), fluidResource.toStack(amountToInsert));
                    if (filteredStack.isEmpty()) {
                        continue;
                    }

                    amountToInsert = filteredStack.getAmount();
                }

                int transferred = ResourceHandlerUtil.move(extractHandler, insertHandler, fr -> fr.equals(fluidResource),
                    amountToInsert, transaction);

                // Deduct the transferred fluid from our maximum transfer.
                maxTransfer -= transferred;
                if (maxTransfer <= 0) {
                    break;
                }
            }

            transaction.commit();
            return maxTransfer;
        }
    }
}
