package com.enderio.enderio.content.machines.solar_panel;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SolarPanelEnergyHandler implements EnergyHandler {
    private final SolarPanelNode node;

    public SolarPanelEnergyHandler(SolarPanelNode node) {
        this.node = node;
    }

    @Override
    public long getAmountAsLong() {
        return node.getNetwork().getTotalEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return node.getNetwork().getTotalMaxEnergyStored();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0) {
            return 0;
        }

        // Initially try to take from the current node
        int extracted = node.extractEnergy(amount, transaction);

        // If we haven't extracted enough, try to take from the network
        if (extracted < amount) {
            for (var otherNode : node.getNetwork().nodes()) {
                if (otherNode == node) {
                    continue;
                }

                extracted += otherNode.extractEnergy(amount - extracted, transaction);
                if (extracted >= amount) {
                    break;
                }
            }
        }

        return extracted;
    }
}
