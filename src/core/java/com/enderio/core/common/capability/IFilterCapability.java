package com.enderio.core.common.capability;

import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.function.Predicate;

public interface IFilterCapability<T> extends Predicate<T> {

    void setNbt(Boolean nbt);

    boolean isNbt();

    void setInverted(Boolean inverted);

    boolean isInvert();

    List<T> getEntries();

    Slot getSlot(int pSlot, int pX, int pY);

    void setEntry(int index, T entry);
}
