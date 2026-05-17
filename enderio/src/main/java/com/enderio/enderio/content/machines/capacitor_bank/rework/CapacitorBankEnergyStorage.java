package com.enderio.enderio.content.machines.capacitor_bank.rework;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class CapacitorBankEnergyStorage implements IEnergyStorage {
    private final CapacitorBankNode node;

    private long added;
    private long send;
    private long lastReset;

    public CapacitorBankEnergyStorage(CapacitorBankNode node) {
        this.node = node;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

        // Initially try to insert to the current node
        int inserted = node.insertEnergy(amount, simulate);

        // If we haven't inserted enough, try to push to the network
        if (inserted < amount) {
            for (var otherNode : node.getNetwork().nodes()) {
                if (otherNode == node) {
                    continue;
                }

                inserted += otherNode.insertEnergy(amount - inserted, simulate);
                if (inserted >= amount) {
                    break;
                }
            }
        }

        added += inserted;
        return inserted;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

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

        send += extracted;
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return (int) node.getNetwork().getTotalEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) node.getNetwork().getTotalMaxEnergyStored();
    }

    public long getEnergyStoredAsLong() {
        return node.getNetwork().getTotalEnergyStored();
    }

    public long getMaxEnergyStoredAsLong() {
        return node.getNetwork().getTotalMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public void reset(long time) {
        if (lastReset != time) {
            added = 0;
            send = 0;
            lastReset = time;
        }
    }
}
