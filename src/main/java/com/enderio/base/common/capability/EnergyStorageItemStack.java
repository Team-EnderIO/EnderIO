package com.enderio.base.common.capability;

import com.enderio.base.EIONBTKeys;
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
        if (!tag.contains(EIONBTKeys.ENERGY)) {
            nbt.putInt(EIONBTKeys.ENERGY_MAX_STORED, capacity);
            nbt.putInt(EIONBTKeys.ENERGY_MAX_RECEIVE, maxReceive);
            nbt.putInt(EIONBTKeys.ENERGY_MAX_EXTRACT, maxExtract);
            nbt.putInt(EIONBTKeys.ENERGY_STORED, energy);
            tag.put(EIONBTKeys.ENERGY, nbt);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY)) {
            var energyStorage = tag.getCompound(EIONBTKeys.ENERGY);
            
            int capacity = energyStorage.getInt(EIONBTKeys.ENERGY_MAX_STORED);
            int maxReceiveInternal = energyStorage.getInt(EIONBTKeys.ENERGY_MAX_RECEIVE);
            int energy = energyStorage.getInt(EIONBTKeys.ENERGY_STORED);

            if (maxReceiveInternal <= 0) {
                return 0;
            }
            int energyReceived = Math.min(capacity - energy, Math.min(maxReceiveInternal, maxReceive));
            if (!simulate) {
                energy += energyReceived;
                energyStorage.putInt(EIONBTKeys.ENERGY_STORED, energy);

            }
            return energyReceived;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY)) {
            var energyStorage = tag.getCompound(EIONBTKeys.ENERGY);
            
            int maxExtractInternal = energyStorage.getInt(EIONBTKeys.ENERGY_MAX_EXTRACT);
            int energy = energyStorage.getInt(EIONBTKeys.ENERGY_STORED);

            if (maxExtractInternal <= 0) {
                return 0;
            }
            int energyExtracted = Math.min(energy, Math.min(maxExtractInternal, maxExtract));
            if (!simulate) {
                energy -= energyExtracted;
                energyStorage.putInt(EIONBTKeys.ENERGY_STORED, energy);
            }
            return energyExtracted;
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY) && tag.getCompound(EIONBTKeys.ENERGY).contains(EIONBTKeys.ENERGY_STORED)) {
            return tag.getCompound(EIONBTKeys.ENERGY).getInt(EIONBTKeys.ENERGY_STORED);
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY) && tag.getCompound(EIONBTKeys.ENERGY).contains(EIONBTKeys.ENERGY_MAX_STORED)) {
            return tag.getCompound(EIONBTKeys.ENERGY).getInt(EIONBTKeys.ENERGY_MAX_STORED);
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY) && tag.getCompound(EIONBTKeys.ENERGY).contains(EIONBTKeys.ENERGY_MAX_EXTRACT)) {
            return tag.getCompound(EIONBTKeys.ENERGY).getInt(EIONBTKeys.ENERGY_MAX_EXTRACT) > 0;
        }
        return false;
    }

    @Override
    public boolean canReceive() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.ENERGY) && tag.getCompound(EIONBTKeys.ENERGY).contains(EIONBTKeys.ENERGY_MAX_RECEIVE)) {
            return tag.getCompound(EIONBTKeys.ENERGY).getInt(EIONBTKeys.ENERGY_MAX_RECEIVE) > 0;
        }
        return false;
    }
}
