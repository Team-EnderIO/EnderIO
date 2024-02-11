package com.enderio.core.common.item;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

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

    // TODO: NEO-PORT: This parameter type is a guess.
    void addAllVariants(Consumer<CreativeModeTab.Output> modifier);
}
