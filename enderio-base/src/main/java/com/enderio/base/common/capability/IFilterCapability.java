package com.enderio.base.common.capability;

import java.util.List;

public interface IFilterCapability<T> {

    void setNbt(Boolean nbt);

    boolean isNbt();

    void setInverted(Boolean inverted);

    boolean isInvert();

    List<T> getEntries();

    void setEntry(int index, T entry);
}
