package com.enderio.api.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public interface IConduitFilter {

    /**
     * Get the items to filter
     * @return This filter refs directly to the one we will save, you <b>MUST</b> make sure there is no {@link Items#AIR AIR} or nulls.
     */
    List<ItemStack> getItems();

    /**
     * Test if the item is in the filter list/valid
     * @param item the item
     */
    boolean testItem(ItemStack item);

    /**
     * Save the data to the itemstack
     */
    void save();

    /**
     * @return size of the inventory, when rendering make sure that each row len is 9
     */
    int getSize();

    boolean getIgnoreMode();
    void setIgnoreMode(boolean newState);

    boolean getStrictMode();
    void setStrictMode(boolean newState);
}
