package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public record EnergyConduitStorage(Direction side, ConduitNode node) implements IEnergyStorage {
    // Shared across capability wrappers, including different nodes and sides of the same network.
    private static final ThreadLocal<Set<ConduitNetwork>> ACTIVE_NETWORKS =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        if (!node.isLoaded() || amount <= 0) {
            return 0;
        }

        if (!node.getConnectionConfig(side, EnergyConduitConnectionConfig.TYPE).canExtract(node::hasRedstoneSignal)) {
            return 0;
        }

        var network = node.getNetwork();
        var activeNetworks = ACTIVE_NETWORKS.get();
        // GH-1466: External capabilities can synchronously proxy energy back into this network,
        // even while simulating. Reject that branch without blocking transfers to other networks.
        if (!activeNetworks.add(network)) {
            return 0;
        }

        try {
            return receiveEnergyIntoNetwork(network, amount, simulate);
        } finally {
            activeNetworks.remove(network);
            if (activeNetworks.isEmpty()) {
                ACTIVE_NETWORKS.remove();
            }
        }
    }

    private int receiveEnergyIntoNetwork(ConduitNetwork network, int amount, boolean simulate) {
        var inserts = network.insertConnectionsFrom(new ConduitBlockConnection(node, side));

        int energyToSend = Math.min(amount, getTransferRate());
        int energyAccepted = 0;
        for (var insert : inserts) {
            // TODO: Temp to try and avoid issues with unloaded nodes sneaking through.
            if (!insert.end().node().isLoaded()) {
                continue;
            }

            // GH-1384: Prevent self-feed. Could add a feature for enabling this later.
            if (insert.end().node() == node && insert.end().connectionSide() == side) {
                continue;
            }

            var energyStorage = insert.end().getSidedCapability(Capabilities.EnergyStorage.BLOCK);
            if (energyStorage == null || !energyStorage.canReceive()) {
                continue;
            }

            // Cap to path speed.
            int toSend = Math.min(energyToSend, insert.property(EnergyConduit.PATH_MAX_TRANSFER_RATE));

            int sent = energyStorage.receiveEnergy(toSend, simulate);
            energyAccepted += sent;
            energyToSend -= sent;
            if (energyToSend <= 0) {
                return energyAccepted;
            }
        }

        return energyAccepted;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    private int getTransferRate() {
        Holder<EnergyConduit> conduit = node().conduit(EIOConduitTypes.ENERGY.get());
        return conduit.value().transferRatePerTick();
    }
}
