package com.enderio.enderio.content.filters.item.limited;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.fml.LogicalSide;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

    @UseOnly(LogicalSide.CLIENT)
    public static LimitedItemFilterMenu openMenuClient(int containerId, Inventory playerInventory,
            RegistryFriendlyByteBuf buf) {
        return new LimitedItemFilterMenu(EIOMenus.LIMITED_ITEM_FILTER.get(), containerId, playerInventory);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var filter = getFilter(stack);

        if (filter.shouldCompareComponents()) {
            tooltipComponents.add(FiltersLang.FILTER_MATCH_COMPONENTS);
        } else {
            tooltipComponents.add(FiltersLang.FILTER_IGNORE_COMPONENTS);
        }

        tooltipComponents.add(FiltersLang.DAMAGE_FILTER_MODE.copy().append(" - ").append(filter.damageFilterMode().getComponent()));
    }
}
