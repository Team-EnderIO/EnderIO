package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.core.common.capability.IFilterCapability;
import com.enderio.core.common.util.NbtUtil;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChemicalFilterCapability implements IFilterCapability<BoxedChemicalStack>, ChemicalStackFilter {

    private static final String INVERTED_KEY = "IsInverted";
    private static final String ENTRIES_KEY = "ChemicalEntries";

    private final ItemStack container;
    private final int size;

    public ChemicalFilterCapability(ItemStack container, int size) {
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
    public void setNbt(Boolean nbt) {
    }

    @Override
    public boolean isNbt() {
        return false;
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
    public int size() {
        return size;
    }

    @Override
    public List<BoxedChemicalStack> getEntries() {
        CompoundTag tag = container.getOrCreateTag();

        List<BoxedChemicalStack> entries = new ArrayList<>();
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
            ensureSize(entriesList);

            for (var entry : entriesList) {
                entries.add(BoxedChemicalStack.read((CompoundTag) entry));
            }
        }

        return entries;
    }

    @Override
    public BoxedChemicalStack getEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }

        CompoundTag tag = container.getOrCreateTag();

        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            return BoxedChemicalStack.EMPTY;
        }

        ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        ensureSize(entriesList);
        return BoxedChemicalStack.read(entriesList.getCompound(index));
    }

    @Override
    public void setEntry(int index, BoxedChemicalStack entry) {
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
        entriesList.set(index, entry.isEmpty() ? new CompoundTag() : entry.write(new CompoundTag()));
    }

    @Override
    public boolean test(BoxedChemicalStack stack) {
        for (BoxedChemicalStack testStack : getEntries()) {
            if (testStack.getType().equals(stack.getType())) {
                return !isInvert();
            }
        }

        return isInvert();
    }

    private void ensureSize(ListTag list) {
        NbtUtil.ensureSize(list, size, CompoundTag::new);
    }
}