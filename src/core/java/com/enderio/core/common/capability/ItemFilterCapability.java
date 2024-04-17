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
    private final boolean advanced;
    private final boolean invert;

    public ItemFilterCapability(int size, boolean advanced, boolean invert) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.advanced = advanced;
        this.invert = invert;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isInvert() {
        return invert;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, items);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    public boolean test(ItemStack stack) {
        for (ItemStack testStack : getItems()) {
            boolean test = isAdvanced() ? ItemStack.isSameItemSameTags(testStack, stack) : ItemStack.isSameItem(testStack, stack);
            if (!isInvert() && test) {
                return true;
            }
        }
        return isInvert();
    }
}
