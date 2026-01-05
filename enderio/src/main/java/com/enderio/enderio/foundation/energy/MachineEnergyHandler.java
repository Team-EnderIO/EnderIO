package com.enderio.enderio.foundation.energy;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

public interface MachineEnergyHandler {
    void set(int energy);

    int add(int energyToAdd, @Nullable TransactionContext transactionContext);

    int subtract(int energyToTake);

    // Allows checking without a transaction.
    boolean canConsumeAtLeast(int energyRequired);

    // Purposefully allows use without a transaction.
    int consume(int maxEnergyToConsume, @Nullable TransactionContext transactionContext);

    int getMaxConsumption();
}
