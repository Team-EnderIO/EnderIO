package com.enderio.core.common.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ItemStackEnergy {

    public static int getMaxEnergyStored(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0;
    }

    public static int getEnergyStored(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage != null ? energyStorage.getEnergyStored() : 0;
    }

    public static boolean hasEnergy(ItemStack stack, int amount) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage != null && energyStorage.getEnergyStored() >= amount;
    }

    public static void setFull(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null) {
            energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false);
        }
    }

    public static void setEmpty(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null) {
            energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
        }
    }

    public static void set(ItemStack stack, int energy) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null) {
            int delta = energy - energyStorage.getEnergyStored();
            if (delta < 0) {
                energyStorage.extractEnergy(-delta, false);
            } else {
                energyStorage.receiveEnergy(delta, false);
            }
        }
    }

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    public static int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage != null ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    public static int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        var energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage != null ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
    }
}
