package com.enderio.core.common.capability;

import com.enderio.core.common.menu.ItemFilterSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemFilterCapability implements IFilterCapability<ItemStack> {

    private final NonNullList<ItemStack> items;
    private boolean nbt;
    private boolean invert;

    public ItemFilterCapability(int size, boolean nbt, boolean invert) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.nbt = nbt;
        this.invert = invert;
    }

    @Override
    public List<ItemStack> getEntries() {
        return items;
    }

    @Override
    public Slot getSlot(int pSlot, int pX, int pY) {
        return new ItemFilterSlot(items, pSlot, pX, pY);
    }

    public void setNbt(Boolean nbt) {
        this.nbt = nbt;
    }

    public boolean isNbt() {
        return nbt;
    }

    public void setInverted(Boolean inverted) {
        this.invert = inverted;
    }

    public boolean isInvert() {
        return invert;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, items);
        tag.putBoolean("nbt", nbt);
        tag.putBoolean("inverted", invert);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
        this.nbt = nbt.getBoolean("nbt");
        this.invert = nbt.getBoolean("inverted");
    }

    @Override
    public boolean test(ItemStack stack) {
        for (ItemStack testStack : getEntries()) {
            boolean test = isNbt() ? ItemStack.isSameItemSameTags(testStack, stack) : ItemStack.isSameItem(testStack, stack);
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }
}
