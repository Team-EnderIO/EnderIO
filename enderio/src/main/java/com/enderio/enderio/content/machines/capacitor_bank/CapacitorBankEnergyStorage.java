package com.enderio.enderio.content.machines.capacitor_bank;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public record CapacitorBankEnergyStorage(CapacitorBankBlockEntity capacitorBank, @Nullable Direction side) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) {
            return 0;
        }

        return capacitorBank.getNetwork().receiveEnergy(capacitorBank.getBlockPos(), side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) {
            return 0;
        }

        return capacitorBank.getNetwork().extractEnergy(capacitorBank.getBlockPos(), side, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return Math.clamp(capacitorBank.getNetwork().getTotalEnergyStored(), 0, Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return Math.clamp(capacitorBank.getNetwork().getTotalMaxEnergyStored(), 0, Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        if (side == null) {
            return true;
        }

        return capacitorBank.getIOMode(side).canOutput();
    }

    @Override
    public boolean canReceive() {
        if (side == null) {
            return true;
        }

        return capacitorBank.getIOMode(side).canInput();
    }

    @Nullable
    public static IEnergyStorage getSided(CapacitorBankBlockEntity capacitorBank, @Nullable Direction side) {
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
