package com.enderio.base.api.soul.binding;

import com.enderio.base.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public class ComponentSoulBindable implements ISoulBindable {

    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<Soul> componentType;

    public ComponentSoulBindable(MutableDataComponentHolder parent, DataComponentType<Soul> componentType) {
        this.parent = parent;
        this.componentType = componentType;
    }

    @Override
    public Soul getBoundSoul() {
        return parent.getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return true;
    }

    @Override
    public void bindSoul(Soul newSoul) {
        if (!canBind()) {
            throw new UnsupportedOperationException("Cannot rebind this item.");
        }

        if (!isSoulValid(newSoul)) {
            throw new IllegalArgumentException("Soul is not valid for this item.");
        }

        parent.set(componentType, newSoul);
    }
}
