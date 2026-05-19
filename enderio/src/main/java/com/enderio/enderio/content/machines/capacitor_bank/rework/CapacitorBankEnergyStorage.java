package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.enderio.foundation.energy.PoweredMachineEnergyStorage;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class CapacitorBankEnergyStorage implements IEnergyStorage {
    private final CapacitorBankNode node;

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

        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return Math.clamp(node.getNetwork().getTotalEnergyStored(), 0, Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return Math.clamp(node.getNetwork().getTotalMaxEnergyStored(), 0, Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Nullable
    public IEnergyStorage getSided(Direction side) {
        if (!node.getBlockEntity().getIOMode(side).canInput() && !node.getBlockEntity().getIOMode(side).canOutput()) {
            return null;
        }

        if (!node.getBlockEntity().getIOMode(side).canConnect()) {
            return null;
        }

        return new SidedAccess(this, side);
    }

    public record SidedAccess(CapacitorBankEnergyStorage wrapped, Direction side) implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive() || maxReceive <= 0) {
                return 0;
            }

            return wrapped.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtract() || maxExtract <= 0) {
                return 0;
            }

            return wrapped.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return wrapped.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return wrapped.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            if (!wrapped.node.getBlockEntity().getIOMode(side).canOutput()) {
                return false;
            }

            return wrapped.canExtract();
        }

        @Override
        public boolean canReceive() {
            if (!wrapped.node.getBlockEntity().getIOMode(side).canInput()) {
                return false;
            }

            return wrapped.canReceive();
        }
    }
}
