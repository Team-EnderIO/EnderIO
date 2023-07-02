package com.enderio.machines.common.item;

import com.enderio.machines.common.block.CapacitorBankBlock;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CapacitorBankItem extends BlockItem {
    public CapacitorBankItem(CapacitorBankBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BlockEntityEnergyStorage(stack, ((CapacitorBankBlock)getBlock()).getTier().getStorageCapacity());
    }

    private static class BlockEntityEnergyStorage implements IEnergyStorage, ICapabilityProvider {

        private final ItemStack container;
        private final int capacity;
        public BlockEntityEnergyStorage(ItemStack container, int capacity) {
            this.container = container;
            this.capacity = capacity;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (simulate)
                return Math.min(maxReceive, getMaxEnergyStored() - getEnergyStored());
            int stored = getEnergyStored();
            int received = Math.min(maxReceive, getMaxEnergyStored() - stored);
            setEnergyStored(stored + received);
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (simulate)
                return Math.min(maxExtract, getEnergyStored());
            int stored = getEnergyStored();
            int extracted = Math.min(maxExtract, stored);
            setEnergyStored(stored - extracted);
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return Optional.ofNullable(container.getTag())
                .filter(nbt -> nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND))
                .map(nbt -> nbt.getCompound("BlockEntityTag"))
                .filter(nbt -> nbt.contains("energy", Tag.TAG_COMPOUND))
                .map(nbt -> nbt.getCompound("energy"))
                .filter(nbt -> nbt.contains("stored", Tag.TAG_INT))
                .map(nbt -> nbt.getInt("stored"))
                .orElse(0);
        }

        public void setEnergyStored(int stored) {
            CompoundTag nbt = container.getOrCreateTag();
            CompoundTag blockEntityTag = null;
            if (nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                blockEntityTag = nbt.getCompound("BlockEntityTag");
            } else {
                blockEntityTag = new CompoundTag();
                nbt.put("BlockEntityTag", blockEntityTag);
            }
            CompoundTag energyTag = null;
            if (blockEntityTag.contains("energy", Tag.TAG_COMPOUND)) {
                energyTag = blockEntityTag.getCompound("energy");
            } else {
                energyTag = new CompoundTag();
                nbt.put("energy", energyTag);
            }
            energyTag.putInt("stored", stored);
        }

        @Override
        public int getMaxEnergyStored() {
            return capacity;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY)
                return LazyOptional.of(() -> this).cast();
            return LazyOptional.empty();
        }
    }
}
