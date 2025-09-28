package com.enderio.base.common.filter.item.mod_id;

import com.enderio.base.api.filter.ItemFilter;
import com.enderio.base.common.filter.AbstractFilterItem;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.function.Supplier;

public class ModIdItemFilterItem extends AbstractFilterItem<ModIdItemFilter> {
    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack, v) -> stack
        .getOrDefault(EIODataComponents.ITEM_FILTER, ModIdItemFilter.EMPTY);

    public ModIdItemFilterItem(Properties properties) {
        super(properties);
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
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
        return new ModIdItemFilterMenu(containerId, playerInventory, filterAccess);
    }
}
