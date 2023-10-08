package com.enderio.api.capability;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ICoordinateSelectionHolder {

    @Nullable
    CoordinateSelection getSelection();

    void setSelection(CoordinateSelection selection);

    default boolean hasSelection() {
        return getSelection() != null;
    }

    default void ifSelectionPresent(Consumer<CoordinateSelection> cons) {
        if (hasSelection()) {
            cons.accept(getSelection());
        }
    }
}
