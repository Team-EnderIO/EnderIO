package com.enderio.core.common.item;

import net.minecraft.world.item.CreativeModeTab;

public interface ICustomCreativeTabEntries {
    default boolean shouldAddDefaultItem() {
        return true;
    }

    default void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    }
}
