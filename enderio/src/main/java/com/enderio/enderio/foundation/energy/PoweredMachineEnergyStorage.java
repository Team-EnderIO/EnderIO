package com.enderio.enderio.foundation.energy;

import com.enderio.core.CoreNBTKeys;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class PoweredMachineEnergyStorage implements EnergyHandler, ValueIOSerializable, MachineEnergyHandler {

    private final PoweredMachineBlockEntity machine;

    private int energyStored;
    private final EnergyJournal energyJournal;

    public PoweredMachineEnergyStorage(PoweredMachineBlockEntity machine) {
        this.machine = machine;
        energyJournal = new EnergyJournal();
    }

    @Override
    public long getAmountAsLong() {
        return energyStored;
    }

    @Override
    public long getCapacityAsLong() {
        return machine.getMaxEnergyStored();
    }

    @Override
    public int getMaxConsumption() {
        return machine.getMaxEnergyUse();
    }

    @Override
    public void set(int energyStored) {
        TransferPreconditions.checkNonNegative(energyStored);
        if (energyStored != this.energyStored) {
            int previousAmount = this.energyStored;
            this.energyStored = energyStored;
            onEnergyChanged(previousAmount);
        }
    }

    protected void onEnergyChanged(int previousAmount) {
    }

    @Override
    public final int add(int energyToAdd, @Nullable TransactionContext transactionContext) {
        TransferPreconditions.checkNonNegative(energyToAdd);

        try (Transaction subTransaction = Transaction.open(transactionContext)) {
            int inserted = Math.min(getCapacityAsInt() - this.energyStored, energyToAdd);

            if (inserted > 0) {
                energyJournal.updateSnapshots(subTransaction);
                energyStored += inserted;
            }

            subTransaction.commit();
            return inserted;
        }
    }

    @Override
    public int subtract(int energyToTake) {
        TransferPreconditions.checkNonNegative(energyToTake);
        int extracted = Math.min(energyStored, energyToTake);

        if (extracted > 0) {
            int energyBefore = this.energyStored;
            energyStored -= extracted;
            onEnergyChanged(energyBefore);
        }

        return extracted;
    }

    @Override
    public boolean canConsumeAtLeast(int energyRequired) {
        TransferPreconditions.checkNonNegative(energyRequired);
        if (energyRequired > getMaxConsumption()) {
            return false;
        }

        return energyStored >= energyRequired;
    }

    @Override
    public int consume(int maxEnergyToConsume, @Nullable TransactionContext transactionContext) {
        TransferPreconditions.checkNonNegative(maxEnergyToConsume);

        try (Transaction subTransaction = Transaction.open(transactionContext)) {
            int consumed = Math.min(energyStored, Math.min(maxEnergyToConsume, getMaxConsumption()));
            if (consumed > 0) {
                energyJournal.updateSnapshots(subTransaction);
                energyStored -= consumed;
            }

            subTransaction.commit();
            return consumed;
        }
    }

    @Nullable
    public EnergyHandler getSided(Direction side) {
        if (!machine.energyIOMode().canInput() && !machine.energyIOMode().canOutput()) {
            return null;
        }

        if (!machine.getIOMode(side).canConnect()) {
            return null;
        }

        return new SidedAccess(this, side);
    }

    // region Directionless Access

    @Override
    public int insert(int amount, TransactionContext transactionContext) {
        TransferPreconditions.checkNonNegative(amount);

        if (!machine.energyIOMode().canInput()) {
            return 0;
        }

        int inserted = Math.min(getCapacityAsInt() - this.energyStored, amount);

        if (MachinesConfig.COMMON.ENERGY.THROTTLE_ENERGY_INPUT.get()) {
            inserted = Math.min(machine.getMaxEnergyUse() * 2, inserted);
        }

        if (inserted > 0) {
            energyJournal.updateSnapshots(transactionContext);
            energyStored += inserted;
        }

        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transactionContext) {
        TransferPreconditions.checkNonNegative(amount);

        if (!machine.energyIOMode().canOutput()) {
            return 0;
        }

        int extracted = Math.min(energyStored, amount);
        if (extracted > 0) {
            energyJournal.updateSnapshots(transactionContext);
            energyStored -= extracted;
        }

        return extracted;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt(CoreNBTKeys.ENERGY_STORED, energyStored);
    }

    @Override
    public void deserialize(ValueInput input) {
        energyStored = input.getIntOr(CoreNBTKeys.ENERGY_STORED, 0);
    }

    // endregion

    public record SidedAccess(PoweredMachineEnergyStorage wrapped, Direction side) implements EnergyHandler {

        @Override
        public long getAmountAsLong() {
            return wrapped.getAmountAsLong();
        }

        @Override
        public long getCapacityAsLong() {
            return wrapped.getCapacityAsLong();
        }

        @Override
        public int insert(int amount, TransactionContext transactionContext) {
            TransferPreconditions.checkNonNegative(amount);

            if (!wrapped.machine.energyIOMode().canInput()) {
                return 0;
            }

            if (side != null && wrapped.machine.energyIOMode().respectIOConfig()
                && !wrapped.machine.getIOMode(side).canInput()) {
                return 0;
            }

            return wrapped.insert(amount, transactionContext);
        }

        @Override
        public int extract(int amount, TransactionContext transactionContext) {
            TransferPreconditions.checkNonNegative(amount);

            if (!wrapped.machine.energyIOMode().canOutput()) {
                return 0;
            }

            if (side != null && wrapped.machine.energyIOMode().respectIOConfig()
                && !wrapped.machine.getIOMode(side).canOutput()) {
                return 0;
            }

            return wrapped.extract(amount, transactionContext);
        }
    }

    private class EnergyJournal extends SnapshotJournal<Integer> {
        protected Integer createSnapshot() {
            return PoweredMachineEnergyStorage.this.energyStored;
        }

        protected void revertToSnapshot(Integer snapshot) {
            PoweredMachineEnergyStorage.this.energyStored = snapshot;
        }

        protected void onRootCommit(Integer originalState) {
            int previousAmount = originalState;
            if (PoweredMachineEnergyStorage.this.energyStored != previousAmount) {
                PoweredMachineEnergyStorage.this.onEnergyChanged(previousAmount);
            }
        }
    }
}
