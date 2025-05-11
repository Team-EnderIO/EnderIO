package com.enderio.modconduits.common.modules.laserio;

import com.direwolf20.laserio.common.containers.FilterBasicContainer;
import com.direwolf20.laserio.common.containers.customhandler.FilterBasicHandler;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.api.filter.ItemFilter;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class LaserItemFilter implements ItemFilter {

    private final ItemStack container;

    public LaserItemFilter(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack test(@Nullable IItemHandler target, ItemStack stack) {
        final boolean isAllowList = container.getOrDefault(LaserIODataComponents.FILTER_ALLOW, true);
        final boolean shouldCompareComponents = container.getOrDefault(LaserIODataComponents.FILTER_COMPARE, false);

        FilterBasicHandler handler = new FilterBasicHandler(FilterBasicContainer.SLOTS, container);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack match = handler.getStackInSlot(i);
            if (!match.isEmpty()) {
                if (shouldCompareComponents ? ItemStack.isSameItem(match, stack)
                        : ItemStack.isSameItemSameComponents(match, stack)) {
                    return isAllowList ? stack : ItemStack.EMPTY;
                }
            }
        }

        return isAllowList ? ItemStack.EMPTY : stack;
    }
}
