package com.enderio.core.common.capability;

import com.enderio.core.common.menu.FluidFilterSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidFilterCapability implements IFilterCapability<FluidStack> {

    private final NonNullList<FluidStack> fluids;

    private boolean nbt;
    private boolean invert;

    public FluidFilterCapability(int size, boolean nbt, boolean invert) {
        this.fluids = NonNullList.withSize(size, FluidStack.EMPTY);
        this.nbt = nbt;
        this.invert = invert;
    }

    @Override
    public void setNbt(Boolean nbt) {
        this.nbt = nbt;
    }

    @Override
    public boolean isNbt() {
        return nbt;
    }

    @Override
    public void setInverted(Boolean inverted) {
        this.invert = inverted;
    }

    @Override
    public boolean isInvert() {
        return invert;
    }

    @Override
    public List<FluidStack> getEntries() {
        return fluids;
    }

    @Override
    public Slot getSlot(int pSlot, int pX, int pY) {
        return new FluidFilterSlot(fluids, pSlot, pX, pY);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        saveAllItems(tag, fluids);
        tag.putBoolean("nbt", nbt);
        tag.putBoolean("inverted", invert);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        loadAllItems(nbt, fluids);
        this.nbt = nbt.getBoolean("nbt");
        this.invert = nbt.getBoolean("inverted");
    }

    @Override
    public boolean test(FluidStack stack) {
        for (FluidStack testStack : getEntries()) {
            boolean test = isNbt() ? testStack.isFluidEqual(stack) : testStack.is(stack.getFluid());
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }

    public static CompoundTag saveAllItems(CompoundTag pTag, NonNullList<FluidStack> pList) {
        ListTag listtag = new ListTag();

        for(int i = 0; i < pList.size(); ++i) {
            FluidStack stack = pList.get(i);
            if (!stack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                stack.writeToNBT(compoundtag);
                listtag.add(compoundtag);
            }
        }

        pTag.put("Fluids", listtag);


        return pTag;
    }

    public static void loadAllItems(CompoundTag pTag, NonNullList<FluidStack> pList) {
        ListTag listtag = pTag.getList("Fluids", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < pList.size()) {
                pList.set(j, FluidStack.loadFluidStackFromNBT(compoundtag));
            }
        }
    }
}
