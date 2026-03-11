package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public record EnergyConduitStorage(Direction side, @Nullable ConduitNode node) implements IEnergyStorage {
    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        if (node == null || !node.isLoaded() || amount <= 0) {
            return 0;
        }

        var network = node.getNetwork();
        var inserts = network.insertConnectionsFrom(new ConduitBlockConnection(node, side));

        int energyToSend = Math.min(amount, getTransferRate());
        int energyAccepted = 0;
        for (var insert : inserts) {
            var energyStorage = insert.end().getSidedCapability(ForgeCapabilities.ENERGY);
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
        return conduit.transferRatePerTick();
    }
}
