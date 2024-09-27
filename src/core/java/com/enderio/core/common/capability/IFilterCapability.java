package com.enderio.core.common.capability;

import java.util.List;
import java.util.function.Predicate;

public interface IFilterCapability<T> {

    void setNbt(Boolean nbt);

    boolean isNbt();

    void setInverted(Boolean inverted);

    boolean isInvert();

    int size();
    
    List<T> getEntries();

    T getEntry(int index);

    void setEntry(int index, T entry);
}
