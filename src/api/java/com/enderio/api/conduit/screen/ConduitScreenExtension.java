package com.enderio.api.conduit.screen;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.misc.Vector2i;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConduitScreenExtension<T extends ConduitData<T>> {

    @FunctionalInterface
    interface UpdateExtendedData<T extends ConduitData<T>> {
        void update(Function<T, T> mapper);
    }

    /**
     * @param conduitDataSupplier       the conduit data the widgets are for, manipulate the state of it in the widgets
     * @param updateConduitData         call this to modify the conduit data.
     * @param direction                 the supplier to get the current direction for this extendedconduitdata
     * @param widgetsStart              the position on which widgets start
     * @return Widgets that manipulate the extended ConduitData, these changes are synced back to the server
     */
    List<AbstractWidget> createWidgets(Screen screen, Supplier<T> conduitDataSupplier, UpdateExtendedData<T> updateConduitData,
        Supplier<Direction> direction, Vector2i widgetsStart);
}
