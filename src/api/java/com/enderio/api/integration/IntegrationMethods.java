package com.enderio.api.integration;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Optional;

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

    /**
     * @param stack The ItemStack a conduit was rightclicked with
     * @return empty Optional if this stack is not a facade item. Or the BlockState this facade disguises as
     */
    default Optional<BlockState> getFacadeOf(ItemStack stack) {
        return Optional.empty();
    }
}
