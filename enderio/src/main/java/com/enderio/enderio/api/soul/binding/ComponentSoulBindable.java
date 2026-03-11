package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.common.MutableDataComponentHolder;

public class ComponentSoulBindable implements SoulBindable {

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
