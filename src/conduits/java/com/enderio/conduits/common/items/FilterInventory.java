package com.enderio.conduits.common.items;

import com.enderio.api.capability.IConduitFilter;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FilterInventory implements Container {
    public final ItemStack filter;

    public FilterInventory(ItemStack is) {
        filter = is;
    }

    /**
     * @param listRef vector2 list of x,y pos of the thingies
     */
    public void getDrawableSlots(List<Vector2i> listRef) {
        var y = -1; // 0 % 9 == 0
        for (int x = 0; x < getContainerSize(); x++) {
            var xMod = (x % 9);
            if (xMod == 0) y++;
            listRef.add(new Vector2i(14 + xMod * 18, 16 + (y * 18)));
        }
    }

    // TODO see if this need caching for perf
    public IConduitFilter getFilterCap() {
        return filter.getCapability(EIOCapabilities.ITEM_FILTER).resolve().get();
    }

    @Override
    public int getContainerSize() {
        return getFilterCap().getSize();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : getFilterCap().getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= getFilterCap().getItems().size() ? ItemStack.EMPTY : getFilterCap().getItems().get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        getFilterCap().save();
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        getFilterCap().getItems().clear();
        getFilterCap().save();
    }
}
