package com.enderio.enderio.client.content.filters.item;

import com.enderio.core.client.gui.tooltip.AbstractClientItemListTooltip;
import com.enderio.enderio.content.filters.item.existing.ExistingItemFilter;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientLimitedItemFilterTooltip extends AbstractClientItemListTooltip {
    private final LimitedItemFilter filter;

    public ClientLimitedItemFilterTooltip(LimitedItemFilter filter) {
        this.filter = filter;
    }

    @Override
    protected List<ItemStack> itemStacksToDisplay() {
        return this.filter.matches().stream().filter(stack -> !stack.isEmpty()).toList();
    }
}
