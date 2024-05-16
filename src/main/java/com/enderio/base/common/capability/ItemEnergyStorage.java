package com.enderio.base.common.capability;

import com.enderio.core.common.components.ItemEnergyStorageConfig;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ItemEnergyStorage implements IEnergyStorage {

    private final Supplier<DataComponentType<Integer>> componentType;
    private final ItemStack stack;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    /**
     * Create an item energy storage, pulling configuration from the Item implementation.
     * @param componentType The name of the data component storing the total energy.
     * @param stack The item stack the energy storage is attached to.
     * @throws IllegalArgumentException when the ItemStack's Item does not implement {@link ItemEnergyStorageConfig}.
     */
    public ItemEnergyStorage(Supplier<DataComponentType<Integer>> componentType, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemEnergyStorageConfig config)) {
            throw new IllegalArgumentException("This constructor can only be used if the stack's Item implements IItemEnergyConfig.");
        }

        this.componentType = componentType;
        this.stack = stack;
        this.capacity = config.getMaxEnergy();
        this.maxReceive = config.getMaxReceive();
        this.maxExtract = config.getMaxExtract();
    }

    public ItemEnergyStorage(Supplier<DataComponentType<Integer>> componentType, ItemStack stack, int capacity) {
        this(componentType, stack, capacity, capacity, capacity, null);
    }

    public ItemEnergyStorage(Supplier<DataComponentType<Integer>> componentType, ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        this(componentType, stack, capacity, capacity, capacity, null);
    }

    public ItemEnergyStorage(Supplier<DataComponentType<Integer>> componentType, ItemStack stack, int capacity, int maxReceive, int maxExtract, @Nullable Integer energy) {
        this.componentType = componentType;
        this.stack = stack;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;

        if (energy != null) {
            stack.set(componentType, energy);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energy = stack.getOrDefault(componentType, 0);

        if (this.maxReceive <= 0) {
            return 0;
        }

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            stack.set(componentType, energy);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energy = stack.getOrDefault(componentType, 0);

        if (this.maxExtract <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            stack.set(componentType, energy);
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return stack.getOrDefault(componentType, 0);
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }
}
