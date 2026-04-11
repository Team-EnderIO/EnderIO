package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public record EnergyConduitStorage(Direction side, @Nullable ConduitNode node) implements EnergyHandler {

    @Override
    public long getAmountAsLong() {
        return 0;
    }

    @Override
    public long getCapacityAsLong() {
        return 0;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (node == null || !node.isLoaded() || amount <= 0) {
            return 0;
        }

        var network = node.getNetwork();
        network.ensureCachesReady();
        var inserts = network.insertConnectionsFrom(new ConduitBlockConnection(node, side));

        int energyToSend = Math.min(amount, getTransferRate());
        int energyAccepted = 0;
        for (var insert : inserts) {
            var energyHandler = insert.end().getSidedCapability(Capabilities.Energy.BLOCK);
            if (energyHandler == null) {
                continue;
            }

            // Cap to path speed.
            int toSend = Math.min(energyToSend, insert.property(EnergyConduit.PATH_MAX_TRANSFER_RATE));

            int sent = energyHandler.insert(toSend, transaction);
            energyAccepted += sent;
            energyToSend -= sent;
            if (energyToSend <= 0) {
                return energyAccepted;
            }
        }

        return energyAccepted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return 0;
    }

    private int getTransferRate() {
        Holder<EnergyConduit> conduit = node().conduit(EIOConduitTypes.ENERGY.get());
        return conduit.value().transferRatePerTick();
    }
}
