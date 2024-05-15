package com.enderio.base.common.menu;

import com.enderio.base.common.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ItemFilterMenu extends FilterMenu{

    public ItemFilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId, inventory, stack);
    }

    public ItemFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        super(EIOMenus.ITEM_FILTER.get(), pContainerId, inventory, stack);
    }

    public static ItemFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new ItemFilterMenu(EIOMenus.ITEM_FILTER.get(), pContainerId, inventory, inventory.player.getMainHandItem());
    }
}
