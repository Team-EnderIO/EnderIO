package com.enderio.base.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageItemStack implements IEnergyStorage {

    private final ItemStack stack;

    public EnergyStorageItemStack(ItemStack stack, int capacity) {
        this(stack, capacity, capacity, capacity, 0);
    }

    public EnergyStorageItemStack(ItemStack stack, int capacity, int maxReceive, int maxExtract, int energy) {
        this.stack = stack;
        CompoundTag tag = this.stack.getOrCreateTag();
        CompoundTag nbt = new CompoundTag();
        if (!tag.contains("EnergyStorage")) {
            nbt.putInt("Capacity", capacity);
            nbt.putInt("MaxReceive", maxReceive);
            nbt.putInt("MaxExtract", maxExtract);
            nbt.putInt("Energy", energy);
            tag.put("EnergyStorage", nbt);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage")) {
            int capacity = tag.getCompound("EnergyStorage").getInt("Capacity");
            int maxReceiveInternal = tag.getCompound("EnergyStorage").getInt("MaxReceive");
            int energy = tag.getCompound("EnergyStorage").getInt("Energy");

            if (maxReceiveInternal <= 0) {
                return 0;
            }
            int energyReceived = Math.min(capacity - energy, Math.min(maxReceiveInternal, maxReceive));
            if (!simulate) {
                energy += energyReceived;
                tag.getCompound("EnergyStorage").putInt("Energy", energy);

            }
            return energyReceived;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage")) {
            int maxExtractInternal = tag.getCompound("EnergyStorage").getInt("MaxExtract");
            int energy = tag.getCompound("EnergyStorage").getInt("Energy");

            if (maxExtractInternal <= 0) {
                return 0;
            }
            int energyExtracted = Math.min(energy, Math.min(maxExtractInternal, maxExtract));
            if (!simulate) {
                energy -= energyExtracted;
                tag.getCompound("EnergyStorage").putInt("Energy", energy);
            }
            return energyExtracted;
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage") && tag.getCompound("EnergyStorage").contains("Energy")) {
            return tag.getCompound("EnergyStorage").getInt("Energy");
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage") && tag.getCompound("EnergyStorage").contains("Capacity")) {
            return tag.getCompound("EnergyStorage").getInt("Capacity");
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage") && tag.getCompound("EnergyStorage").contains("MaxExtract")) {
            return tag.getCompound("EnergyStorage").getInt("MaxExtract") > 0;
        }
        return false;
    }

    @Override
    public boolean canReceive() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains("EnergyStorage") && tag.getCompound("EnergyStorage").contains("MaxReceive")) {
            return tag.getCompound("EnergyStorage").getInt("MaxReceive") > 0;
        }
        return false;
    }
}
