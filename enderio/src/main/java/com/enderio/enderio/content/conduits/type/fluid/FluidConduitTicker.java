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

                IFluidHandler extractHandler = extractConnection.getSidedCapability(Capabilities.FluidHandler.BLOCK);
                if (extractHandler == null) {
                    continue;
                }

                final var extractConduit = extractConnection.node().conduit(conduitType());
                final int fluidRate = extractConduit.value().transferRatePerTick() * conduitType().getTickRate(extractConduit);

                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {
                    if (extractHandler.getFluidInTank(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                    remaining = doFluidTransfer(fluid, remaining, extractConnection, insertPaths, insertedPerPath);
                }
            }
        }
    }

    private int doFluidTransfer(Fluid fluid, int maxTransfer, ConduitBlockConnection extractConnection,
        List<ConduitConnectionPath> insertPaths, Map<ConduitConnectionPath, Integer> insertedPerPath) {
        var extractHandler = Objects
            .requireNonNull(extractConnection.getSidedCapability(Capabilities.FluidHandler.BLOCK));

        // Attempt to drain fluid from the target.
        FluidStack extractedFluid = extractHandler.drain(new FluidStack(fluid, maxTransfer),
            IFluidHandler.FluidAction.SIMULATE);
        if (extractedFluid.isEmpty()) {
            return maxTransfer;
        }

        // Test the extracted fluid against the target
        var extractFilter = extractConnection.inventory()
            .getStackInSlot(FluidConduit.EXTRACT_FILTER_SLOT)
            .getCapability(EnderIOCapabilities.FLUID_FILTER);

        if (extractFilter != null) {
            extractedFluid = extractFilter.test(extractHandler, extractedFluid);
            if (extractedFluid.isEmpty()) {
                return maxTransfer;
            }
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

            IFluidHandler insertHandler = insertConnection.getSidedCapability(Capabilities.FluidHandler.BLOCK);
            if (insertHandler == null) {
                continue;
            }

            var fluidToInsert = extractedFluid.copy();

            // Limit to path's max speed
            if (fluidToInsert.getAmount() > remaining) {
                fluidToInsert.setAmount(remaining);
            }

            // Test fluid against insert filter.
            var insertFilter = insertConnection.inventory()
                .getStackInSlot(FluidConduit.INSERT_FILTER_SLOT)
                .getCapability(EnderIOCapabilities.FLUID_FILTER);

            if (insertFilter != null) {
                fluidToInsert = insertFilter.test(insertHandler, fluidToInsert);
                if (fluidToInsert.isEmpty()) {
                    continue;
                }
            }

            // Attempt to transfer fluid.
            FluidStack transferredFluid = FluidUtil.tryFluidTransfer(insertHandler, extractHandler, fluidToInsert,
                true);

            // Deduct the transferred fluid from our maximum transfer.
            maxTransfer -= transferredFluid.getAmount();

            // Store the amount transferred in this path
            insertedPerPath.compute(insertPath, (k, v) -> v == null ? transferredFluid.getAmount() : v + transferredFluid.getAmount());

            if (maxTransfer <= 0) {
                break;
            }
        }

        return maxTransfer;
    }
}
