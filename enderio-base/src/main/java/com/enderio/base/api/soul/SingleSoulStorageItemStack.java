package com.enderio.base.api.soul;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class SingleSoulStorageItemStack implements SingleSoulStorage {

    protected final Supplier<DataComponentType<Soul>> componentType;
    protected ItemStack container;

    public SingleSoulStorageItemStack(Supplier<DataComponentType<Soul>> componentType,
            ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @Override
    public Soul getSoul() {
        return container.getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public void setSoul(Soul soul) {
        if (soul.hasEntity()) {
            container.set(componentType, soul);
        } else {
            container.remove(componentType);
        }
    }
}
