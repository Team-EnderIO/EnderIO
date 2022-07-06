package com.enderio.api.integration;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * These are all the methods a Integration can override or call.
 * Please make sure that all methods only reference API or Minecraft classes, so that this can be part of the API, after stable release
 */
public interface IntegrationMethods {

    default void createData(GatherDataEvent event) {
    }

    /**
     * @param stack The ItemStack used to mine the block
     * @return if this ItemStack can mine blocks directly
     */
    default boolean canMineWithDirect(ItemStack stack) {
        return false;
    }
}
