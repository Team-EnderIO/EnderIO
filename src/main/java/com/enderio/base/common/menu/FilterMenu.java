package com.enderio.base.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.core.common.capability.ItemFilterCapability;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FilterMenu extends AbstractContainerMenu {

    public FilterMenu(int pContainerId, ItemStack stack) {
        super(null, pContainerId);
        ItemFilterCapability capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
        NonNullList<ItemStack> items = capability.getItems();
        for (int i = 0; i < items.size(); i++) {
            addSlot(new FilterSlot(items, i, 0,0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
