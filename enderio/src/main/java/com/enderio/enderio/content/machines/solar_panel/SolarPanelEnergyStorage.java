package com.enderio.enderio.content.machines.solar_panel;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class SolarPanelEnergyStorage implements IEnergyStorage {
    private final SolarPanelNode node;

    public SolarPanelEnergyStorage(SolarPanelNode node) {
        this.node = node;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        // Initially try to take from the current node
        int extracted = node.extractEnergy(amount, simulate);

        // If we haven't extracted enough, try to take from the network
        if (extracted < amount) {
            for (var otherNode : node.getNetwork().nodes()) {
                if (otherNode == node) {
                    continue;
                }

                extracted += otherNode.extractEnergy(amount - extracted, simulate);
                if (extracted >= amount) {
                    break;
                }
            }
        }

        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return node.getNetwork().getTotalEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return node.getNetwork().getTotalMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
