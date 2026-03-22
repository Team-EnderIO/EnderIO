package com.enderio.enderio.content.conduits.type.fluid;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.enderio.init.EIOConduitTypes;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FluidConduitTicker extends ConduitTickerBase<FluidConduit> {

    public static final FluidConduitTicker INSTANCE = new FluidConduitTicker();

    private FluidConduitTicker() {
        super(EIOConduitTypes.FLUID::get);
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits) {
        Map<ConduitConnectionPath, Integer> insertedPerPath = Maps.newHashMap();

        for (var channel : network.allChannels()) {
            for (var extractConnection : network.extractConnections(channel)) {
                var insertPaths = network.insertConnectionsFrom(extractConnection);
                if (insertPaths.isEmpty()) {
                    continue;
                }

                ResourceHandler<FluidResource> extractHandler = extractConnection.getSidedCapability(Capabilities.Fluid.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                final var extractConduit = extractConnection.node().conduit(conduitType());
                final int fluidRate = extractConduit.value().transferRatePerTick() * conduitType().getTickRate(extractConduit);

                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.size() && remaining > 0; i++) {
                    if (extractHandler.getResource(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getResource(i).getFluid();
                    remaining = doFluidTransfer(fluid, remaining, extractConnection, insertPaths, insertedPerPath);
                }
            }
        }
    }

    private int doFluidTransfer(Fluid fluid, int maxTransfer, ConduitBlockConnection extractConnection,
        List<ConduitConnectionPath> insertPaths, Map<ConduitConnectionPath, Integer> insertedPerPath) {

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
            for (var insertPath : insertPaths) {
                var insertConnection = insertPath.end();
                final int maxInsertSpeed = insertPath.property(FluidConduit.PATH_MAX_TRANSFER_RATE);

                // Calculate remaining 'speed' for this path
                int remaining = maxInsertSpeed - insertedPerPath.getOrDefault(insertPath, 0);
                if (remaining <= 0) {
                    continue;
                }

                var insertHandler = insertConnection.getSidedCapability(Capabilities.Fluid.BLOCK);
                if (insertHandler == null) {
                    continue;
                }

                int amountToInsert = maxExtract;
                if (amountToInsert > remaining) {
                    amountToInsert = remaining;
                }

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

                // Attempt to transfer fluid.
                int transferred = ResourceHandlerUtil.move(extractHandler, insertHandler, fr -> fr.equals(fluidResource),
                    amountToInsert, transaction);

                // Deduct the transferred fluid from our maximum transfer.
                maxTransfer -= transferred;

                // Store the amount transferred in this path
                insertedPerPath.compute(insertPath, (k, v) -> v == null ? transferred : v + transferred);

                if (maxTransfer <= 0) {
                    break;
                }
            }

            transaction.commit();
            return maxTransfer;
        }
    }
}
