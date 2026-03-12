package com.enderio.enderio.content.filters.item.limited;

import com.enderio.core.common.backports.DataComponentType;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraft.world.item.ItemStack;

public class LimitedItemFilterItem extends AbstractFilterItem<LimitedItemFilter> {

    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.LIMITED_ITEM_FILTER, LimitedItemFilter.EMPTY);

    public LimitedItemFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    protected DataComponentType<LimitedItemFilter> dataComponentType() {
        return EIODataComponents.LIMITED_ITEM_FILTER;
    }

    @Override
    protected LimitedItemFilter defaultFilter() {
        return LimitedItemFilter.EMPTY;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            AbstractFilterMenu.FilterAccess filterAccess) {
        return new LimitedItemFilterMenu(EIOMenus.LIMITED_ITEM_FILTER.get(), containerId, playerInventory,
                filterAccess);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static LimitedItemFilterMenu openMenuClient(int containerId, Inventory playerInventory,
            FriendlyByteBuf buf) {
        return new LimitedItemFilterMenu(EIOMenus.LIMITED_ITEM_FILTER.get(), containerId, playerInventory);
    }
}
