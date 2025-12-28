package com.enderio.core.common.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ItemStackEnergy {

    public static int getMaxEnergyStored(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energyStorage != null ? energyStorage.getCapacityAsInt() : 0;
    }

    public static int getEnergyStored(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energyStorage != null ? energyStorage.getAmountAsInt() : 0;
    }

    public static boolean hasEnergy(ItemStack stack, int amount) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energyStorage != null && energyStorage.getAmountAsInt() >= amount;
    }

    public static void setFull(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));

        if (energyStorage != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                energyStorage.insert(energyStorage.getCapacityAsInt(), transaction);
                transaction.commit();
            }
        }
    }

    public static void setEmpty(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));

        if (energyStorage != null) {
            try (Transaction transaction = Transaction.openRoot()) {
                energyStorage.extract(energyStorage.getAmountAsInt(), transaction);
                transaction.commit();
            }
        }
    }

    public static void set(ItemStack stack, int energy) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));

        if (energyStorage != null) {

            try (Transaction transaction = Transaction.openRoot()) {
                int delta = energy - energyStorage.getAmountAsInt();

                if (delta < 0) {
                    energyStorage.extract(-delta, transaction);
                } else {
                    energyStorage.insert(delta, transaction);
                }

                transaction.commit();
            }
        }
    }

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive  Maximum amount of energy to be inserted.
     * @param transaction Transaction for operation.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    public static int insertEnergy(ItemStack stack, int maxReceive, TransactionContext transaction) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energyStorage != null ? energyStorage.insert(maxReceive, transaction) : 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract  Maximum amount of energy to be extracted.
     * @param transaction Transaction for operation.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    public static int extractEnergy(ItemStack stack, int maxExtract, TransactionContext transaction) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        return energyStorage != null ? energyStorage.extract(maxExtract, transaction) : 0;
    }
}
