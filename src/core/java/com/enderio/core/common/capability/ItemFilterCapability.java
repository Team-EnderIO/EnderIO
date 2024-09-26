package com.enderio.core.common.capability;

import com.enderio.api.filter.ItemStackFilter;
import com.enderio.core.common.util.NbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFilterCapability implements IFilterCapability<ItemStack>, ItemStackFilter {

    private static final String NBT_KEY = "IsNbt";
    private static final String INVERTED_KEY = "IsInverted";
    private static final String ENTRIES_KEY = "ItemEntries";

    private final ItemStack container;
    private final int size;

    public ItemFilterCapability(ItemStack container, int size) {
        this.container = container;
        this.size = size;

        CompoundTag tag = container.getOrCreateTag();
        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = new ListTag();
            ensureSize(entriesList);
            tag.put(ENTRIES_KEY, entriesList);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<ItemStack> getEntries() {
        CompoundTag tag = container.getOrCreateTag();

        List<ItemStack> entries = new ArrayList<>();
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
            ensureSize(entriesList);

            for (var entry : entriesList) {
                entries.add(ItemStack.of((CompoundTag) entry));
            }
        }

        return entries;
    }

    @Override
    public ItemStack getEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }

        CompoundTag tag = container.getOrCreateTag();

        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            return ItemStack.EMPTY;
        }

        ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        ensureSize(entriesList);
        return ItemStack.of((CompoundTag) entriesList.get(index));
    }

    @Override
    public void setEntry(int index, ItemStack stack) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }

        CompoundTag tag = container.getOrCreateTag();

        ListTag entriesList;
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        } else {
            entriesList = new ListTag();
            tag.put(ENTRIES_KEY, entriesList);
        }

        ensureSize(entriesList);
        entriesList.set(index, stack.serializeNBT());
    }

    @Override
    public void setNbt(Boolean nbt) {
        CompoundTag tag = container.getOrCreateTag();
        tag.putBoolean(NBT_KEY, nbt);
    }

    @Override
    public boolean isNbt() {
        CompoundTag tag = container.getOrCreateTag();
        return tag.contains(NBT_KEY, CompoundTag.TAG_BYTE) && tag.getBoolean(NBT_KEY);
    }

    @Override
    public void setInverted(Boolean inverted) {
        CompoundTag tag = container.getOrCreateTag();
        tag.putBoolean(INVERTED_KEY, inverted);
    }

    @Override
    public boolean isInvert() {
        CompoundTag tag = container.getOrCreateTag();
        return tag.contains(INVERTED_KEY, CompoundTag.TAG_BYTE) && tag.getBoolean(INVERTED_KEY);
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

    private void ensureSize(ListTag list) {
        NbtUtil.ensureSize(list, size, () -> ItemStack.EMPTY.save(new CompoundTag()));
    }
}
