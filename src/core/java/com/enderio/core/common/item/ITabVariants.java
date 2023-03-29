package com.enderio.core.common.item;

import com.tterrag.registrate.util.CreativeModeTabModifier;

/**
 * If an item implements multiple variants that should be displayed, use this.
 * TODO: Might PR this into Registrate
 */
@FunctionalInterface
public interface ITabVariants {
    /**
     * Add all variants to the tab modifier
     * @param modifier The modifier.
     */

    void addAllVariants(CreativeModeTabModifier modifier);
}
