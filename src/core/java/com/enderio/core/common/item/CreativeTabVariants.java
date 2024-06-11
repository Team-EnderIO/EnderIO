package com.enderio.core.common.item;

import net.minecraft.world.item.CreativeModeTab;

/**
 * If an item implements multiple variants that should be displayed, use this.
 */
@FunctionalInterface
public interface CreativeTabVariants {
    /**
     * Add all variants to the tab modifier
     * @param modifier The modifier.
     */

    void addAllVariants(CreativeModeTab.Output modifier);
}
