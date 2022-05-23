package com.enderio.api.capability;

import com.enderio.api.nbt.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;

public interface ICoordinateSelectionHolder extends INamedNBTSerializable<Tag> {

    @Override
    default String getSerializedName() {
        return "CoordinateSelection";
    }

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
