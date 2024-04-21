package com.enderio.core.common.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemFilterCapability implements INBTSerializable<CompoundTag>, Predicate<ItemStack> {

    private final NonNullList<ItemStack> items;
    private boolean nbt;
    private boolean invert;

    public ItemFilterCapability(int size, boolean nbt, boolean invert) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.nbt = nbt;
        this.invert = invert;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
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
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
        this.nbt = nbt.getBoolean("nbt");
        this.invert = nbt.getBoolean("inverted");
    }

    @Override
    public boolean test(ItemStack stack) {
        for (ItemStack testStack : getItems()) {
            boolean test = isNbt() ? ItemStack.isSameItemSameTags(testStack, stack) : ItemStack.isSameItem(testStack, stack);
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }
}
