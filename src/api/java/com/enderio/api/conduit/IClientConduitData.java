package com.enderio.api.conduit;

import com.enderio.api.misc.IIcon;
import com.enderio.api.misc.Vector2i;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public interface IClientConduitData<T extends IExtendedConduitData<T>> extends IIcon {

    @Override
    default Vector2i getIconSize() {
        return new Vector2i(24, 24);
    }

    @Override
    default Vector2i getRenderSize() {
        return new Vector2i(12, 12);
    }

    /**
     *
     * @param extendedConduitData the extendedconduitdata the widgets are fore, manipulate the state of it in the widgets
     * @param direction the supplier to get the current direction for this extendedconduitdata
     * @param widgetsStart the position on which widgets start
     * @return Widgets that manipulate the extended ConduitData, these changes are synced back to the server
     */
    List<AbstractWidget> createWidgets(T extendedConduitData, Supplier<Direction> direction, Vector2i widgetsStart);

    record Simple<T extends IExtendedConduitData<T>>(ResourceLocation getTextureLocation, Vector2i getTexturePosition) implements IClientConduitData<T> {
        @Override
        public List<AbstractWidget> createWidgets(T extendedConduitData, Supplier<Direction> direction, Vector2i widgetsStart) {
            return List.of();
        }
    }
}
