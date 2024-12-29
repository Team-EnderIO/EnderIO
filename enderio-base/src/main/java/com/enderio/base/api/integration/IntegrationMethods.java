package com.enderio.base.api.integration;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * These are all the methods an Integration can override or call.
 */
public interface IntegrationMethods {

    default void createData(GatherDataEvent event) {
    }

    default void onModConstruct() {

    }

    /**
     * @param stack The ItemStack used to mine the block
     * @return if this ItemStack can mine blocks directly
     */
    default boolean canMineWithDirect(ItemStack stack) {
        return false;
    }
}
