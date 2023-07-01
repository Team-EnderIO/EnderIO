package com.enderio.api.capability;

import java.util.function.Consumer;

public interface ICoordinateSelectionHolder {

    CoordinateSelection getSelection();

    void setSelection(CoordinateSelection selection);

    default boolean hasSelection() {
        return getSelection() != null;
    }

    default void ifSelectionPresent(Consumer<CoordinateSelection> cons) {
        if (hasSelection())
            cons.accept(getSelection());
    }
}
