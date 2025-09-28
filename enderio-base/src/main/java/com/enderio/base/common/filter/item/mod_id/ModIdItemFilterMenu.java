package com.enderio.base.common.filter.item.mod_id;

import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.menu.CoordinateMenu;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ModIdItemFilterMenu extends AbstractFilterMenu<ModIdItemFilter> {
    public ModIdItemFilterMenu(int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        super(EIOMenus.MOD_ID_ITEM_FILTER.get(), containerId, playerInventory, filterAccess);
    }

    protected ModIdItemFilterMenu(int containerId, Inventory playerInventory) {
        super(EIOMenus.MOD_ID_ITEM_FILTER.get(), containerId, playerInventory);
    }

    public static ModIdItemFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new ModIdItemFilterMenu(pContainerId, inventory);
    }

    @Override
    protected Supplier<DataComponentType<ModIdItemFilter>> dataComponentType() {
        return EIODataComponents.MOD_ID_ITEM_FILTER;
    }

    @Override
    protected ModIdItemFilter defaultFilter() {
        return ModIdItemFilter.EMPTY;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }
}
