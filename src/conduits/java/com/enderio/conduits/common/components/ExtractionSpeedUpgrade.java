package com.enderio.conduits.common.components;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class ExtractionSpeedUpgrade implements ConduitUpgrade {
    private final Supplier<DataComponentType<Integer>> componentType;
    private final ItemStack itemStack;

    public ExtractionSpeedUpgrade(Supplier<DataComponentType<Integer>> componentType, ItemStack itemStack) {
        this.componentType = componentType;
        this.itemStack = itemStack;
    }

    public int tier() {
        return itemStack.getOrDefault(componentType.get(), 0);
    }
}
