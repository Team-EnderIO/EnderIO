package com.enderio.base.common.capability;

import java.util.List;

// TODO: Rename this? Should only be implemented by filters that can be given UI by Ender IO
public interface IFilterCapability<T> {

    // TODO: some of these are optional for filters - maybe need a way to opt-out of these methods.
    void setNbt(Boolean nbt);

    boolean isNbt();

    void setInverted(Boolean inverted);

    boolean isInvert();

    int size();

    List<T> getEntries();

    T getEntry(int index);

    void setEntry(int index, T entry);
}
