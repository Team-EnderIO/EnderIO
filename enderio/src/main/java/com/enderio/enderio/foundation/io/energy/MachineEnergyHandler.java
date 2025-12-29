package com.enderio.enderio.foundation.io.energy;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public interface MachineEnergyHandler {
    void set(int energy);

    int add(int energyToAdd, @Nullable TransactionContext transactionContext);

    int subtract(int energyToTake);

    // Purposefully allows use without a transaction.
    int consume(int maxEnergyToConsume, @Nullable TransactionContext transactionContext);

    int getMaxConsumption();
}
