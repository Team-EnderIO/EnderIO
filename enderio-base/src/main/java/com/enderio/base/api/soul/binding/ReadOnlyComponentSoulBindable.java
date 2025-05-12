package com.enderio.base.api.soul.binding;

import com.enderio.base.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public class ReadOnlyComponentSoulBindable implements ISoulBindable {

    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<Soul> componentType;

    public ReadOnlyComponentSoulBindable(MutableDataComponentHolder parent, DataComponentType<Soul> componentType) {
        this.parent = parent;
        this.componentType = componentType;
    }

    @Override
    public Soul getBoundSoul() {
        return parent.getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return false;
    }

    @Override
    public void bindSoul(Soul newSoul) {
        throw new UnsupportedOperationException("Cannot rebind this item.");
    }
}
