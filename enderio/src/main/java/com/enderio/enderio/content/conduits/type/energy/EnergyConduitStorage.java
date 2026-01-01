package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.primitives.Ints;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public record EnergyConduitStorage(@Nullable Direction side, int transferRate, @Nullable ConduitNode node) implements EnergyHandler {

    private static final long ENERGY_BUFFER_SCALER = 4;

    @Override
    public long getAmountAsLong() {
        if (node == null || !node.isLoaded()) {
            return 0;
        }

        var context = node.getNetwork().getContext(EnergyConduitNetworkContext.TYPE);
        if (context == null) {
            return 0;
        }

        return Math.max(Math.min(getCapacityAsLong(), context.energyStored()), 0);
    }

    @Override
    public long getCapacityAsLong() {
        if (node == null || !node.isLoaded()) {
            return 0;
        }

        // Capacity is transfer rate + nodeCount * transferRatePerTick / 2 (expanded).
        // This ensures at least the transfer rate of the cable is available, but
        // capacity doesn't grow outrageously.
        int nodeCount = node.getNetwork().nodeCount();

        // The maximum number of nodes before the network capacity is max size.
        long maxNodesBeforeLimit = Long.MAX_VALUE / (transferRate() / ENERGY_BUFFER_SCALER) - ENERGY_BUFFER_SCALER;
        if (nodeCount >= maxNodesBeforeLimit) {
            return Long.MAX_VALUE;
        }

        // Always full transfer rate plus the extra buffer.
        return transferRate() + nodeCount * (transferRate() / ENERGY_BUFFER_SCALER);
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (node == null || !node.isLoaded() || !canInsert()) {
            return 0;
        }

        var context = node.getNetwork().getOrCreateContext(EnergyConduitNetworkContext.TYPE);

        // Cap to transfer rate.
        int toReceive = Math.min(transferRate(), amount);

        int energyReceived = Ints.saturatedCast(Math.min(getCapacityAsLong() - getAmountAsLong(), toReceive));
        context.setEnergyStored(getAmountAsLong() + energyReceived, transaction);

        return energyReceived;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return 0;
    }

    public boolean canInsert() {
        if (side == null || node == null) {
            return false;
        }

        // Only allow extraction if we're configured to allow it.
        if (!node.isConnectedToBlock(side)) {
            return false;
        }

        var config = node.getConnectionConfig(side, EnergyConduitConnectionConfig.TYPE);
        if (!config.isConnected()) {
            return false;
        }

        boolean hasRedstoneSignal = node.hasRedstoneSignal(config.extractRedstoneChannel());
        return config.isExtract() && config.extractRedstoneControl().isActive(hasRedstoneSignal);
    }
}
