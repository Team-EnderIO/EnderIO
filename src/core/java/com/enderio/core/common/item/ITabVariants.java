package com.enderio.core.common.item;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

/**
 * If an item implements multiple variants that should be displayed, use this.
 */
@FunctionalInterface
public interface ITabVariants {
    /**
     * Add all variants to the tab modifier
     * @param modifier The modifier.
     */

    void addAllVariants(CreativeModeTab.Output modifier);
}
