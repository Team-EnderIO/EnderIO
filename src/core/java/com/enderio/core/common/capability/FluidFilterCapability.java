package com.enderio.core.common.capability;

import com.enderio.api.filter.FluidStackFilter;
import com.enderio.core.common.util.NbtUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public class FluidFilterCapability implements IFilterCapability<FluidStack>, FluidStackFilter {

    private static final String NBT_KEY = "IsNbt";
    private static final String INVERTED_KEY = "IsInverted";
    private static final String ENTRIES_KEY = "FluidEntries";

    private final ItemStack container;
    private final int size;

    public FluidFilterCapability(ItemStack container, int size) {
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
    public int size() {
        return size;
    }

    @Override
    public List<FluidStack> getEntries() {
        CompoundTag tag = container.getOrCreateTag();

        List<FluidStack> entries = new ArrayList<>();
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
            ensureSize(entriesList);

            for (var entry : entriesList) {
               entries.add(FluidStack.loadFluidStackFromNBT((CompoundTag) entry));
            }
        }

        return entries;
    }

    @Override
    public FluidStack getEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }

        CompoundTag tag = container.getOrCreateTag();

        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            return FluidStack.EMPTY;
        }

        ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        ensureSize(entriesList);
        return FluidStack.loadFluidStackFromNBT((CompoundTag) entriesList.get(index));
    }

    @Override
    public void setEntry(int index, FluidStack entry) {
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
        entriesList.set(index, entry.writeToNBT(new CompoundTag()));
    }

    @Override
    public boolean test(FluidStack stack) {
        for (FluidStack testStack : getEntries()) {
            boolean test = isNbt() ? FluidStack.areFluidStackTagsEqual(testStack, stack) : testStack.isFluidEqual(stack);
            if (test) {
                return !isInvert();
            }
        }

        return isInvert();
    }

    private void ensureSize(ListTag list) {
        NbtUtil.ensureSize(list, size, () -> FluidStack.EMPTY.writeToNBT(new CompoundTag()));
    }
}
