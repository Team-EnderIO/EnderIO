package com.enderio.enderio.content.machines.capacitor_bank;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public record CapacitorBankEnergyStorage(CapacitorBankBlockEntity capacitorBank, @Nullable Direction side) implements EnergyHandler {

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (!canReceive() || amount <= 0) {
            return 0;
        }

        return capacitorBank.getNetwork().receiveEnergy(capacitorBank.getBlockPos(), side, amount, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (!canExtract() || amount <= 0) {
            return 0;
        }

        return capacitorBank.getNetwork().extractEnergy(capacitorBank.getBlockPos(), side, amount, transaction);
    }

    @Override
    public long getAmountAsLong() {
        return capacitorBank.getNetwork().getTotalEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return capacitorBank.getNetwork().getTotalMaxEnergyStored();
    }

    public boolean canExtract() {
        if (side == null) {
            return true;
        }

        return capacitorBank.getIOMode(side).canOutput();
    }

    public boolean canReceive() {
        if (side == null) {
            return true;
        }

        return capacitorBank.getIOMode(side).canInput();
    }

    @Nullable
    public static EnergyHandler getSided(CapacitorBankBlockEntity capacitorBank, @Nullable Direction side) {
        if (!capacitorBank.getLevel().isClientSide() && side != null) {
            if (!capacitorBank.getIOMode(side).canInput() && !capacitorBank.getIOMode(side).canOutput()) {
                return null;
            }

            if (!capacitorBank.getIOMode(side).canConnect()) {
                return null;
            }
        }

        return new CapacitorBankEnergyStorage(capacitorBank, side);
    }
}
