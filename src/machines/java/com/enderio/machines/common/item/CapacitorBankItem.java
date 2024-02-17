package com.enderio.machines.common.item;

import com.enderio.core.CoreNBTKeys;
import com.enderio.machines.common.block.CapacitorBankBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Optional;

public class CapacitorBankItem extends BlockItem {

    // TODO: NEO-PORT: This is gross...
    public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER =
        (stack, v) -> new BlockEntityEnergyStorage(stack, ((CapacitorBankBlock)((BlockItem)stack.getItem()).getBlock()).getTier().getStorageCapacity());

    public CapacitorBankItem(CapacitorBankBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    private static class BlockEntityEnergyStorage implements IEnergyStorage {

        private final ItemStack container;
        private final int capacity;
        BlockEntityEnergyStorage(ItemStack container, int capacity) {
            this.container = container;
            this.capacity = capacity;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (simulate) {
                return Math.min(maxReceive, getMaxEnergyStored() - getEnergyStored());
            }

            int stored = getEnergyStored();
            int received = Math.min(maxReceive, getMaxEnergyStored() - stored);
            setEnergyStored(stored + received);
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (simulate) {
                return Math.min(maxExtract, getEnergyStored());
            }

            int stored = getEnergyStored();
            int extracted = Math.min(maxExtract, stored);
            setEnergyStored(stored - extracted);
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return Optional.ofNullable(container.getTag())
                .filter(nbt -> nbt.contains(BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND))
                .map(nbt -> nbt.getCompound(BLOCK_ENTITY_TAG))
                .filter(nbt -> nbt.contains(CoreNBTKeys.ENERGY, Tag.TAG_COMPOUND))
                .map(nbt -> nbt.getCompound(CoreNBTKeys.ENERGY))
                .filter(nbt -> nbt.contains(CoreNBTKeys.ENERGY_STORED, Tag.TAG_INT))
                .map(nbt -> nbt.getInt(CoreNBTKeys.ENERGY_STORED))
                .orElse(0);
        }

        public void setEnergyStored(int stored) {
            CompoundTag nbt = container.getOrCreateTag();
            CompoundTag blockEntityTag = null;
            if (nbt.contains(BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
                blockEntityTag = nbt.getCompound(BLOCK_ENTITY_TAG);
            } else {
                blockEntityTag = new CompoundTag();
                nbt.put(BLOCK_ENTITY_TAG, blockEntityTag);
            }
            CompoundTag energyTag = null;
            if (blockEntityTag.contains(CoreNBTKeys.ENERGY, Tag.TAG_COMPOUND)) {
                energyTag = blockEntityTag.getCompound(CoreNBTKeys.ENERGY);
            } else {
                energyTag = new CompoundTag();
                nbt.put(CoreNBTKeys.ENERGY, energyTag);
            }
            energyTag.putInt(CoreNBTKeys.ENERGY_STORED, stored);
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
    }
}
